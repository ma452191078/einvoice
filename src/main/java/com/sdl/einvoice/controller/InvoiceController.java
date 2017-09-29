package com.sdl.einvoice.controller;

import com.google.gson.Gson;
import com.sdl.einvoice.config.InvoiceConfig;
import com.sdl.einvoice.constant.InvoiceConstant;
import com.sdl.einvoice.domain.*;
import com.sdl.einvoice.util.BASE64Util;
import com.sdl.einvoice.util.HttpUtil;
import com.sdl.einvoice.util.InvoiceUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @RequestMapping("/createInvoice")
    public String createInvoice(RHInvoice rhInvoice){
        log.info("开始执行");
        Map<String, Object> result = new HashMap<>();
        String actionUrl = InvoiceConstant.DEV_CREATE_URL;

        actionUrl = actionUrl.replace(InvoiceConstant.APPCODE, invoiceConfig.getAppCode());
        actionUrl = actionUrl.replace(InvoiceConstant.CMDNAME, invoiceConfig.getCmdName());
        actionUrl = actionUrl.replace(InvoiceConstant.SIGN, BASE64Util.getRevFromBase64(invoiceConfig.getSign().getBytes()));

        RHCreateInvoice createInvoice = new RHCreateInvoice();

        //TODO 创建订单信息
        RHOrder rhOrder = new RHOrder();

        //TODO 补充开票人发票信息
        log.info("补充开票人信息");
        rhInvoice.setTaxpayerCode(invoiceConfig.getTaxpayerCode());
        rhInvoice.setTaxpayerTel(invoiceConfig.getTaxpayerTel());
        rhInvoice.setTaxpayerAddress(invoiceConfig.getTaxpayerAddress());
        rhInvoice.setTaxpayerBankAccount(invoiceConfig.getTaxpayerBankAccount());
        rhInvoice.setTaxpayerBankName(invoiceConfig.getTaxpayerBankName());


        //TODO 创建发票行项目
//        RHInvoiceItem rhInvoiceItem = new RHInvoiceItem();
//        List<RHInvoiceItem> items = new ArrayList<>();
//        items.add(rhInvoiceItem);
//        rhInvoice.setItems(items);

        //TODO 通知信息
        List<RHNotices> noticesList = new ArrayList<>();
        RHNotices rhNotices = new RHNotices();
        rhNotices.setType("sms");
        rhNotices.setValue("18265186760");
        noticesList.add(rhNotices);

        createInvoice.setInvoice(rhInvoice);
        createInvoice.setOrder(rhOrder);
        createInvoice.setNotices(noticesList);
        createInvoice.setExtendedParams(null);
        createInvoice.setSerialNo(InvoiceUtil.getSerialNo());
        createInvoice.setPostTime(InvoiceUtil.getPostTime());

//        转换为json
        Gson gson = new Gson();
        String requestJson = gson.toJson(createInvoice);
        log.info(requestJson);
//        String sr = HttpUtil.sendPost(actionUrl, requestJson);
//        log.debug(sr);
//
//        SyncResult syncResult = gson.fromJson(sr, SyncResult.class);
//
        result.put("SERIALNO", createInvoice.getSerialNo());
//        result.put("CODE", syncResult.getCode());
//        result.put("MESSAGE", syncResult.getMessage());

        return gson.toJson(result);
    }



    @RequestMapping("/notifyStanley")
    public String notifyStanley(ResultInvoice resultInvoice){
        String result = "failed";
        if (resultInvoice != null && !"".equals(resultInvoice.getCode())){
            if (resultInvoice.getCode().equals(InvoiceConstant.CODE_SUCCESS)){
                //TODO 发票处理成功

                //TODO 组装数据

                //TODO 调用RFC

            } else {
                //TODO 发票处理失败
            }
        }




        return result;
    }



}
