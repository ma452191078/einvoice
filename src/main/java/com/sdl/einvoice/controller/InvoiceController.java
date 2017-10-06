package com.sdl.einvoice.controller;

import com.google.gson.Gson;
import com.sap.conn.jco.JCoException;
import com.sdl.einvoice.config.InvoiceConfig;
import com.sdl.einvoice.constant.InvoiceConstant;
import com.sdl.einvoice.domain.*;
import com.sdl.einvoice.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
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

    /**
     * 创建发票
     * @param rhInvoice
     * @return
     */
    @RequestMapping("/createInvoice")
    public String createInvoice(RHInvoice rhInvoice) throws Exception {
        log.info("开始执行");
        Map<String, Object> result = new HashMap<>();

        RHCreateInvoice createInvoice = new RHCreateInvoice();

        // 创建订单信息
        RHOrder rhOrder = new RHOrder();

        // 补充开票人发票信息
        log.info("补充开票人信息");
        rhInvoice.setTaxpayerCode(invoiceConfig.getTaxpayerCode());
        rhInvoice.setTaxpayerTel(invoiceConfig.getTaxpayerTel());
        rhInvoice.setTaxpayerAddress(invoiceConfig.getTaxpayerAddress());
        rhInvoice.setTaxpayerBankAccount(invoiceConfig.getTaxpayerBankAccount());
        rhInvoice.setTaxpayerBankName(invoiceConfig.getTaxpayerBankName());

        // 创建发票
        createInvoice.setInvoice(rhInvoice);
        createInvoice.setOrder(rhOrder);
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
                requestJson.getBytes("UTF-8"),
                invoiceConfig.getKeyStorePath(),
                invoiceConfig.getKeyStoreAbner(),
                invoiceConfig.getKeyStorePassWord()
        );
        log.info("签名字符串：" + sign);
        actionUrl = actionUrl.replace(InvoiceConstant.SIGN, sign);
        actionUrl = actionUrl.replace(InvoiceConstant.APPCODE, invoiceConfig.getAppCode());
        actionUrl = actionUrl.replace(InvoiceConstant.CMDNAME, invoiceConfig.getCmdName());

//        String responseJson = HttpUtil.sendPost(actionUrl, requestJson);
//        log.debug("响应报文" + responseJson);
//
//        SyncResult syncResult = gson.fromJson(sr, SyncResult.class);
//
        result.put("SERIALNO", createInvoice.getSerialNo());
//        result.put("CODE", syncResult.getCode());
//        result.put("MESSAGE", syncResult.getMessage());

        return gson.toJson(result);
    }

    /**
     * 红冲发票接口
     * @return
     */
    @RequestMapping("/writeoffInvoice")
    public String writeoffInvoice(RHRedInvoice redInvoice) throws Exception {
        Gson gson = new Gson();
        log.info("开始执行");
        Map<String, Object> result = new HashMap<>();

        redInvoice.setSerialNo(InvoiceUtil.getSerialNo());
        redInvoice.setPostTime(InvoiceUtil.getPostTime());
        redInvoice.setNotices(null);
        redInvoice.setExtendedParams(null);

        String requestJson = gson.toJson(redInvoice);
        String actionUrl = InvoiceConstant.DEV_CREATE_URL;
        String sign = CertificateUtils.signToBase64(requestJson.getBytes("UTF-8"), invoiceConfig.getKeyStorePath(), invoiceConfig.getKeyStoreAbner(), invoiceConfig.getKeyStorePassWord());
        log.info("签名字符串：" + sign);
        actionUrl = actionUrl.replace(InvoiceConstant.SIGN, sign);
        actionUrl = actionUrl.replace(InvoiceConstant.APPCODE, invoiceConfig.getAppCode());
        actionUrl = actionUrl.replace(InvoiceConstant.CMDNAME, invoiceConfig.getCmdName());

//        String responseJson = HttpUtil.sendPost(actionUrl, requestJson);
//        log.debug("响应报文" + responseJson);
//
//        SyncResult syncResult = gson.fromJson(sr, SyncResult.class);
//
        result.put("SERIALNO", redInvoice.getSerialNo());
//        result.put("CODE", syncResult.getCode());
//        result.put("MESSAGE", syncResult.getMessage());
        return gson.toJson(result);
    }


    /**
     * 发票处理回调接口
     * @param resultInvoice
     * @return
     */
    @RequestMapping("/notifyStanley")
    public String notifyStanley(SyncResult resultInvoice){
        String result = "failed";
        if (resultInvoice != null && !"".equals(resultInvoice.getCode())){
            // 发票处理成功
            SAPUtil sapUtil = new SAPUtil(null);
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
                }else {
                    log.info("sap执行失败，{}",exportParam.get("OMSG"));
                }
            } catch (JCoException e) {
                log.info("sap执行失败，{}",e.getMessageText());
                e.printStackTrace();
            }
        }

        return result;
    }



}
