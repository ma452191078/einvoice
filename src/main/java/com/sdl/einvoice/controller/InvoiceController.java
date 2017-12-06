package com.sdl.einvoice.controller;

import com.google.gson.Gson;
import com.sdl.einvoice.config.InvoiceConfig;
import com.sdl.einvoice.config.SapConfig;
import com.sdl.einvoice.constant.InvoiceConstant;
import com.sdl.einvoice.domain.*;
import com.sdl.einvoice.mq.RocketMQProducer;
import com.sdl.einvoice.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 发票操作
 * @author majingyuan
 * @date Create in 2017/9/26 15:31
 */

@RestController
@RequestMapping("/invoice")
@Slf4j
public class InvoiceController {

    @Autowired
    private InvoiceConfig invoiceConfig;

    private static String encode = "UTF-8";

    @Autowired
    private SapConfig sapConfig;

    @Autowired
    private RocketMQProducer rocketMQProducer;

    /**
     * 创建发票
     * @param rhInvoice 开票数据
     * @return 返回开票信息给SAP
     */
    @RequestMapping("/createInvoice")
    public String createInvoice(@RequestBody RHInvoice rhInvoice) throws Exception {
        log.info("===============史丹利电子发票===============");
        log.info("==============开始执行开票操作==============");
        Map<String, Object> result = new HashMap<>();

        RHCreateInvoice createInvoice = new RHCreateInvoice();

        // 创建订单信息
        // 补充开票人发票信息
        log.info("补充开票人信息");

        rhInvoice.setTaxpayerCode(invoiceConfig.getTaxpayerCode());
        rhInvoice.setTaxpayerTel(invoiceConfig.getTaxpayerTel());
        rhInvoice.setTaxpayerAddress(invoiceConfig.getTaxpayerAddress());
        rhInvoice.setTaxpayerBankAccount(invoiceConfig.getTaxpayerBankAccount());
        rhInvoice.setTaxpayerBankName(invoiceConfig.getTaxpayerBankName());
        if (rhInvoice.getItems() != null && rhInvoice.getItems().size() > 0){
            for (int i = 0; i < rhInvoice.getItems().size(); i++) {
                if ("1".equals(rhInvoice.getItems().get(i).getType())){
                    rhInvoice.getItems().get(i).setUom(null);
                    rhInvoice.getItems().get(i).setSpec(null);
                }
            }
        }

        //创建订单
        RHOrder order = new RHOrder();
        order.setOrderNo(rhInvoice.getRemark());
        order.setAccount(rhInvoice.getCustomerName());

        // 创建发票
        createInvoice.setInvoice(rhInvoice);
        createInvoice.setOrder(order);
        createInvoice.setNotices(null);
        createInvoice.setExtendedParams(null);
        createInvoice.setSerialNo(InvoiceUtil.getSerialNo());
        createInvoice.setPostTime(InvoiceUtil.getPostTime());

        // 转换为json
        Gson gson = new Gson();
        String requestJson = gson.toJson(createInvoice);
        log.info("请求报文：" + requestJson);
        //请求接口参数拼装
        String actionUrl = InvoiceConstant.PRD_CREATE_URL;
        String sign = CertificateUtils.signToBase64(
                requestJson.getBytes(encode),
                invoiceConfig.getKeyStorePath(),
                invoiceConfig.getKeyStoreAbner(),
                invoiceConfig.getKeyStorePassWord()
        );
        log.info("签名字符串：" + sign);
        Map<String, String> vars = new HashMap<String, String>();
        vars.put("appCode", URLEncoder.encode(invoiceConfig.getAppCode(), encode));
        vars.put("cmdName", URLEncoder.encode(InvoiceConstant.CMD_CREATE, encode));
        vars.put("sign", URLEncoder.encode(sign, encode));
        // 发送请求
        String responseJson = HttpUtil.doPost(actionUrl, vars, requestJson, 10000, 10000);
        log.info("请求URL：" + actionUrl);
        log.info("响应报文：" + responseJson);

        AsyncResult syncResult = gson.fromJson(responseJson, AsyncResult.class);
        // 返回SAP结果
        result.put("SERIALNO", syncResult.getSerialNo());
        result.put("CODE", syncResult.getCode());
        result.put("MESSAGE", syncResult.getMessage());
        log.info("===============结束==============");
        return gson.toJson(result);
    }

    /**
     * 红冲发票接口
     * @param redInvoice 红冲数据
     * @return 返回开票信息给SAP
     * @throws Exception
     */
    @RequestMapping("/writeoffInvoice")
    public String writeoffInvoice(@RequestBody RHRedInvoice redInvoice) throws Exception {
        Gson gson = new Gson();
        log.info("===============史丹利电子发票===============");
        log.info("==============开始执行红冲操作==============");
        Map<String, Object> result = new HashMap<>();
        // 拼装请求参数
        redInvoice.setSerialNo(InvoiceUtil.getSerialNo());
        redInvoice.setPostTime(InvoiceUtil.getPostTime());
        if(redInvoice.getItems() != null && redInvoice.getItems().size() == 0){
            redInvoice.setItems(null);
        }
        String requestJson = gson.toJson(redInvoice);
        log.info("请求报文：" + requestJson);
        String actionUrl = InvoiceConstant.PRD_CREATE_URL;
        String sign = CertificateUtils.signToBase64(
                requestJson.getBytes(encode),
                invoiceConfig.getKeyStorePath(),
                invoiceConfig.getKeyStoreAbner(),
                invoiceConfig.getKeyStorePassWord()
        );
        log.info("签名字符串：" + sign);
        Map<String, String> vars = new HashMap<String, String>();
        vars.put("appCode", URLEncoder.encode(invoiceConfig.getAppCode(), encode));
        vars.put("cmdName", URLEncoder.encode(InvoiceConstant.CMD_RED, encode));
        vars.put("sign", URLEncoder.encode(sign, encode));

        // 发送请求
        String responseJson = HttpUtil.doPost(actionUrl, vars, requestJson, 10000, 10000);
        log.info("请求URL:" + actionUrl);
        log.info("响应报文：" + responseJson);

        // 解析JSON
        AsyncResult syncResult = gson.fromJson(responseJson, AsyncResult.class);

        //返回结果给SAP
        result.put("SERIALNO", syncResult.getSerialNo());
        result.put("CODE", syncResult.getCode());
        result.put("MESSAGE", syncResult.getMessage());
        log.info("===============结束==============");
        return gson.toJson(result);
    }


    /**
     * 发票处理回调接口
     * @param resultInvoice 回调参数
     * @return 成功返回success，未返回或返回其他值进行3次重试
     */
    @RequestMapping("/notifyStanley")
    public String notifyStanley(@RequestBody SyncResult resultInvoice){
        String result = "failed";
        String MESSAGE_OK = "SEND_OK";
        log.info("===============史丹利电子发票================");
        log.info("===============开始执行回调操作==============");
        log.info("回调报文：{}",resultInvoice.toString());
        if (resultInvoice.getInvoices() != null && !"".equals(resultInvoice.getCode())){
            // 解析数据
            SAPNotify sapNotify = convertResultToSapnotify(resultInvoice);
            // 发送给队列
            Gson gson = new Gson();
            String messageReturn = sendMessage(gson.toJson(sapNotify));
            if (MESSAGE_OK.equals(messageReturn)){
                result = "success";
            }
        }
        log.info("===============结束==============");
        return result;
    }

    /**
     * 查询发票
     * @param criteria 查询条件
     * @return 发票信息
     * @throws Exception
     */
    @RequestMapping("/searchInvoice")
    public String searchInvoice(@RequestBody Criteria criteria) throws Exception{

        Gson gson = new Gson();
        log.info("===============史丹利电子发票==============");
        log.info("==============开始执行查询操作==============");
        Map<String, Object> result = new HashMap<>();
        RHSearchInvoice searchInvoice = new RHSearchInvoice();
        searchInvoice.setSerialNo(InvoiceUtil.getSerialNo());
        searchInvoice.setPostTime(InvoiceUtil.getPostTime());
        if(criteria != null && !"".equals(criteria.getValue())){
            criteria.setName(InvoiceConstant.SEARCH_TYPE_SERIAL_NO);
            List<Criteria> criteriaList = new ArrayList<>();
            criteriaList.add(criteria);
            searchInvoice.setCriteria(criteriaList);
        }else{
            searchInvoice.setCriteria(null);
        }
        String requestJson = gson.toJson(searchInvoice);
        log.info("请求报文：" + requestJson);
        String actionUrl = InvoiceConstant.PRD_CREATE_URL;
        String sign = CertificateUtils.signToBase64(
                requestJson.getBytes(encode),
                invoiceConfig.getKeyStorePath(),
                invoiceConfig.getKeyStoreAbner(),
                invoiceConfig.getKeyStorePassWord()
        );
        log.info("签名字符串：" + sign);
        Map<String, String> vars = new HashMap<String, String>();
        vars.put("appCode", URLEncoder.encode(invoiceConfig.getAppCode(), encode));
        vars.put("cmdName", URLEncoder.encode(InvoiceConstant.CMD_SEARCH, encode));
        vars.put("sign", URLEncoder.encode(sign, encode));
        String responseJson = HttpUtil.doPost(actionUrl, vars, requestJson, 10000, 10000);
        log.info("请求URL:" + actionUrl);
        log.info("响应报文：" + responseJson);

        SyncResult syncResult = gson.fromJson(responseJson, SyncResult.class);

        // 组装数据
        SAPNotify sapNotify = convertResultToSapnotify(syncResult);
        log.info("===============结束==============");
        return gson.toJson(sapNotify);
    }

    /**
     * 发送消息
     * @param gson json数据
     */
    public String sendMessage(String gson){
        //发送到队列
        Message message = new Message();
        message.setBody(gson.getBytes());
        String result = rocketMQProducer.send(message);
        return result;
    }

    /**
     * 返回值转换
     * @param syncResult
     * @return
     */
    private SAPNotify convertResultToSapnotify(SyncResult syncResult){
        SAPNotify sapNotify = new SAPNotify();
        sapNotify.setSerialNo(syncResult.getSerialNo());
        sapNotify.setCode(syncResult.getCode());
        sapNotify.setMessage(syncResult.getMessage());
        if (syncResult.getInvoices() != null && syncResult.getInvoices().size() > 0){
            sapNotify.setOrderNo(syncResult.getInvoices().get(0).getOrdreNo());
            sapNotify.setOriCode(syncResult.getInvoices().get(0).getCode());
            sapNotify.setStatus(syncResult.getInvoices().get(0).getStatus());
            sapNotify.setGentime(syncResult.getInvoices().get(0).getGenerateTime());
            sapNotify.setPdfUrl(syncResult.getInvoices().get(0).getPdfUnsignedUrl());
            sapNotify.setViewUrl(syncResult.getInvoices().get(0).getViewUrl());
            sapNotify.setRelatedCode(syncResult.getInvoices().get(0).getRelatedCode());
            sapNotify.setValidReason(syncResult.getInvoices().get(0).getValidReason());
            sapNotify.setValidTime(syncResult.getInvoices().get(0).getValidTime());
            sapNotify.setTAmount(syncResult.getInvoices().get(0).getTotalAmount());
            sapNotify.setNoTax(syncResult.getInvoices().get(0).getNoTaxAmount());
            sapNotify.setTaxAmount(syncResult.getInvoices().get(0).getTaxAmount());
        }
        return sapNotify;
    }
}
