package com.sdl.einvoice.controller;

import com.sdl.einvoice.config.MQConfig;
import com.sdl.einvoice.mq.RocketMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author majingyuan
 * @Date Create in 2017/12/5 16:50
 */
@RestController
@RequestMapping("/mq")
public class MQController {

    @Autowired
    MQConfig mqConfig;
    @Autowired
    RocketMQProducer rocketMQProducer;

    @RequestMapping("/testProducter")
    public String testProducter(){
        String mesg = "{\"serialNo\":\"9ece8f06-08a0-4481-99e3-628e1e6036a4\",\"code\":\"0\",\"message\":\"处理成功。\",\"oriCode\":\"13702161512536169832\",\"status\":\"1\",\"gentime\":\"2017-12-06 12:56:09\",\"pdfUrl\":\"http://www.chinaeinv.com:980/pdfUnsigned.jspa?c\\u003dF0806709BF7F0A9DA1A2\",\"viewUrl\":\"http://www.chinaeinv.com:980/p.jspa?c\\u003dF0806709BF7F0A9DA1A2\",\"tAmount\":61850.0,\"noTax\":55720.72,\"taxAmount\":6129.28}";
        Message message = new Message();
        message.setBody(mesg.getBytes());
        rocketMQProducer.send(message);
        return null;
    }
}
