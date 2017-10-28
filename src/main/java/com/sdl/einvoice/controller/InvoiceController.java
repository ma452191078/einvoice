package com.sdl.einvoice.controller;

import com.google.gson.Gson;
import com.sap.conn.jco.JCoException;
import com.sdl.einvoice.config.InvoiceConfig;
import com.sdl.einvoice.config.SapConfig;
import com.sdl.einvoice.constant.InvoiceConstant;
import com.sdl.einvoice.domain.*;
import com.sdl.einvoice.util.*;
import lombok.extern.slf4j.Slf4j;
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
 * @Author majingyuan
 * @Date Create in 2017/9/26 15:31
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

    /**
     * 创建发票
     * @param rhInvoice
     * @return
     */
    @RequestMapping("/createInvoice")
    public String createInvoice(@RequestBody RHInvoice rhInvoice) throws Exception {
        log.info("===============史丹利电子发票==============");
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

//        转换为json
        Gson gson = new Gson();
        String requestJson = gson.toJson(createInvoice);
        log.info("请求报文：" + requestJson);

        String actionUrl = InvoiceConstant.DEV_CREATE_URL;
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

        String responseJson = HttpUtil1.doPost(actionUrl, vars, requestJson, 10000, 10000);
        log.info("请求URL：" + actionUrl);
        log.info("响应报文：" + responseJson);

        AsyncResult syncResult = gson.fromJson(responseJson, AsyncResult.class);
//
        result.put("SERIALNO", syncResult.getSerialNo());
        result.put("CODE", syncResult.getCode());
        result.put("MESSAGE", syncResult.getMessage());
        log.info("===============结束==============");
        return gson.toJson(result);
    }

    /**
     * 红冲发票接口
     * @return
     */
    @RequestMapping("/writeoffInvoice")
    public String writeoffInvoice(@RequestBody RHRedInvoice redInvoice) throws Exception {
        Gson gson = new Gson();
        log.info("===============史丹利电子发票==============");
        log.info("==============开始执行红冲操作==============");
        Map<String, Object> result = new HashMap<>();

        redInvoice.setSerialNo(InvoiceUtil.getSerialNo());
        redInvoice.setPostTime(InvoiceUtil.getPostTime());
        if(redInvoice.getItems() != null && redInvoice.getItems().size() == 0){
            redInvoice.setItems(null);
        }
        String requestJson = gson.toJson(redInvoice);
        log.info("请求报文：" + requestJson);
        String actionUrl = InvoiceConstant.DEV_CREATE_URL;
        String sign = CertificateUtils.signToBase64(
                requestJson.getBytes("UTF-8"),
                invoiceConfig.getKeyStorePath(),
                invoiceConfig.getKeyStoreAbner(),
                invoiceConfig.getKeyStorePassWord()
        );
        log.info("签名字符串：" + sign);
        Map<String, String> vars = new HashMap<String, String>();
        vars.put("appCode", URLEncoder.encode(invoiceConfig.getAppCode(), encode));
        vars.put("cmdName", URLEncoder.encode(InvoiceConstant.CMD_RED, encode));
        vars.put("sign", URLEncoder.encode(sign, encode));
        String responseJson = HttpUtil1.doPost(actionUrl, vars, requestJson, 10000, 10000);
        log.info("请求URL:" + actionUrl);
        log.info("响应报文：" + responseJson);

        AsyncResult syncResult = gson.fromJson(responseJson, AsyncResult.class);

        result.put("SERIALNO", syncResult.getSerialNo());
        result.put("CODE", syncResult.getCode());
        result.put("MESSAGE", syncResult.getMessage());
        log.info("===============结束==============");
        return gson.toJson(result);
    }


    /**
     * 发票处理回调接口
     * @param resultInvoice
     * @return
     */
    @RequestMapping("/notifyStanley")
    public String notifyStanley(@RequestBody SyncResult resultInvoice){
        String result = "failed";
        log.info("===============史丹利电子发票==============");
        log.info("===============开始执行回调操作==============");
        log.info("回调报文：{}",resultInvoice.toString());
        if (resultInvoice.getInvoices() != null && !"".equals(resultInvoice.getCode())){
            // 发票处理成功
            SAPUtil sapUtil = new SAPUtil(sapConfig);
            //function名称
            String functionName = "Z_SDL_RH_NOTIFY";
            HashMap<String, Object> exportParam;
            // 组装数据
            SAPNotify sapNotify = new SAPNotify();
            sapNotify.setSerialNo(resultInvoice.getSerialNo());
            sapNotify.setCode(resultInvoice.getCode());
            sapNotify.setMessage(resultInvoice.getMessage());
            sapNotify.setOrderNo(resultInvoice.getInvoices().get(0).getOrdreNo());
            sapNotify.setOriCode(resultInvoice.getInvoices().get(0).getCode());
            sapNotify.setStatus(resultInvoice.getInvoices().get(0).getStatus());
            sapNotify.setGentime(resultInvoice.getInvoices().get(0).getGenerateTime());
            sapNotify.setPdfUrl(resultInvoice.getInvoices().get(0).getPdfUnsignedUrl());
            sapNotify.setViewUrl(resultInvoice.getInvoices().get(0).getViewUrl());
            sapNotify.setRelatedCode(resultInvoice.getInvoices().get(0).getRelatedCode());
            sapNotify.setValidReason(resultInvoice.getInvoices().get(0).getValidReason());
            sapNotify.setValidTime(resultInvoice.getInvoices().get(0).getValidTime());

            // 传入参数
            Gson gson = new Gson();
            HashMap<String, String> importParam = new HashMap<>();
            importParam.put("IJSON", gson.toJson(sapNotify));

            // 传出参数
            HashMap<String,Object> returnParam = new HashMap<>();
            returnParam.put("OFLAG","");
            returnParam.put("OMSG","");

            // 调用RFC
            try {
                exportParam = sapUtil.executeSapFun(functionName,importParam,null,returnParam);
                if (exportParam.get("OFLAG").equals("0")){
                    result = "success";
                    log.info("sap执行成功，数据已接收");
                    System.out.println("sap执行成功，数据已接收");
                }else {
                    log.info("sap执行失败，{}",exportParam.get("OMSG"));
                    System.out.println("sap执行失败，" + exportParam.get("OMSG"));
                }
            } catch (JCoException e) {
                log.info("sap执行失败，{}",e.getMessage());
                System.out.println("sap执行失败，" + e.getMessage());
                e.printStackTrace();
            }
        }
        log.info("===============结束==============");
        return result;
    }

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
        String actionUrl = InvoiceConstant.DEV_CREATE_URL;
        String sign = CertificateUtils.signToBase64(
                requestJson.getBytes("UTF-8"),
                invoiceConfig.getKeyStorePath(),
                invoiceConfig.getKeyStoreAbner(),
                invoiceConfig.getKeyStorePassWord()
        );
        log.info("签名字符串：" + sign);
        Map<String, String> vars = new HashMap<String, String>();
        vars.put("appCode", URLEncoder.encode(invoiceConfig.getAppCode(), encode));
        vars.put("cmdName", URLEncoder.encode(InvoiceConstant.CMD_SEARCH, encode));
        vars.put("sign", URLEncoder.encode(sign, encode));
        String responseJson = HttpUtil1.doPost(actionUrl, vars, requestJson, 10000, 10000);
        log.info("请求URL:" + actionUrl);
        log.info("响应报文：" + responseJson);

        SyncResult syncResult = gson.fromJson(responseJson, SyncResult.class);

        // 组装数据
        SAPNotify sapNotify = new SAPNotify();
        sapNotify.setSerialNo(syncResult.getSerialNo());
        sapNotify.setCode(syncResult.getCode());
        sapNotify.setMessage(syncResult.getMessage());
        sapNotify.setOrderNo(syncResult.getInvoices().get(0).getOrdreNo());
        sapNotify.setOriCode(syncResult.getInvoices().get(0).getCode());
        sapNotify.setStatus(syncResult.getInvoices().get(0).getStatus());
        sapNotify.setGentime(syncResult.getInvoices().get(0).getGenerateTime());
        sapNotify.setPdfUrl(syncResult.getInvoices().get(0).getPdfUnsignedUrl());
        sapNotify.setViewUrl(syncResult.getInvoices().get(0).getViewUrl());
        sapNotify.setRelatedCode(syncResult.getInvoices().get(0).getRelatedCode());
        sapNotify.setValidReason(syncResult.getInvoices().get(0).getValidReason());
        sapNotify.setValidTime(syncResult.getInvoices().get(0).getValidTime());

        log.info("===============结束==============");
        return gson.toJson(sapNotify);
    }

}
