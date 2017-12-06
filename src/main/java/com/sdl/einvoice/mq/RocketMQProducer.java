package com.sdl.einvoice.mq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;

import java.util.UUID;

/**
 * 生产者
 * @Author majingyuan
 * @Date Create in 2017/12/1 15:43
 */
@Slf4j
public class RocketMQProducer {
    private DefaultMQProducer sender;

    protected String nameServer;

    protected String groupName;

    protected String topics;

    /**
     * 初始化
     */
    public void init() {
        sender = new DefaultMQProducer(groupName);
        sender.setNamesrvAddr(nameServer);
        sender.setInstanceName(UUID.randomUUID().toString());
        try {
            sender.start();
            //关闭钩子
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    if (sender != null) {
                        log.info("正在关闭RocketMQ的生产者");
                        sender.shutdown();
                    }
                }
            });
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param nameServer 消息服务器IP
     * @param groupName 生产者组名
     * @param topics  主题标签
     */
    public RocketMQProducer(String nameServer, String groupName, String topics) {
        this.nameServer = nameServer;
        this.groupName = groupName;
        this.topics = topics;
    }

    /**
     * 发送消费信息
     * @param message 要发送的消息
     */
    public String send(Message message) {

        message.setTopic(topics);
        String returnStatus = "";
        try {
            SendResult result = sender.send(message);
            SendStatus status = result.getSendStatus();
            returnStatus = status.toString();
            System.out.println("messageId=" + result.getMsgId() + ", status=" + status);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnStatus;
    }
}
