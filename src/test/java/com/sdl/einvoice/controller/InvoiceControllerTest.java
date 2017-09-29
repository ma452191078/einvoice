package com.sdl.einvoice.controller;

import com.google.gson.Gson;
import com.sdl.einvoice.config.InvoiceConfig;
import com.sdl.einvoice.domain.*;
import com.sdl.einvoice.util.InvoiceUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @Author majingyuan
 * @Date Create in 2017/9/26 16:03
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class InvoiceControllerTest {

    @Autowired
    private InvoiceConfig invoiceConfig;

    @Test
    @RequestMapping("/createInvoice")
    public void createInvoice(){
        Map<String, Object> result = new HashMap<>();

        RHCreateInvoice createInvoice = new RHCreateInvoice();

        //创建订单信息
        RHOrder rhOrder = new RHOrder();

        //创建发票信息
        RHInvoice rhInvoice = new RHInvoice();

        //创建发票行项目
        RHInvoiceItem rhInvoiceItem = new RHInvoiceItem();
        List<RHInvoiceItem> items = new ArrayList<>();
        items.add(rhInvoiceItem);
        rhInvoice.setItems(items);

        List<RHNotices> noticesList = new ArrayList<>();
        RHNotices rhNotices = new RHNotices();
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
        System.out.println(requestJson);
    }
}