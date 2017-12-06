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

        Message message = new Message();
        message.setBody(("I send message to RocketMQ ").getBytes());
        rocketMQProducer.send(message);
        return null;
    }
}
