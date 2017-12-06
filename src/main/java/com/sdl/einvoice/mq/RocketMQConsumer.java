package com.sdl.einvoice.mq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.MessageListener;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;

import java.util.UUID;

/**
 * MQ的消费者
 * @Author majingyuan
 * @Date Create in 2017/12/1 15:35
 */
@Slf4j
public class RocketMQConsumer {
    private DefaultMQPushConsumer consumer;

    private MessageListener listener;

    protected String nameServer;

    protected String groupName;

    protected String topics;

    /**
     *
     * @param listener 监听器
     * @param nameServer    消息服务器
     * @param groupName 分组名
     * @param topics    标签
     */
    public RocketMQConsumer(MessageListener listener, String nameServer, String groupName, String topics) {
        this.listener = listener;
        this.nameServer = nameServer;
        this.groupName = groupName;
        this.topics = topics;
    }

    /**
     * 初始化
     */
    public void init() {
        consumer = new DefaultMQPushConsumer(groupName);
        consumer.setNamesrvAddr(nameServer);
        try {
            consumer.subscribe(topics, "*");
        } catch (MQClientException e) {
            e.printStackTrace();
        }
        consumer.setInstanceName(UUID.randomUUID().toString());
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        consumer.registerMessageListener((MessageListenerConcurrently) this.listener);

        try {
            consumer.start();
            //关闭钩子
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    if (consumer != null) {
                        log.info("正在关闭RocketMQ的消费者");
                        consumer.shutdown();
                    }
                }
            });
        } catch (MQClientException e) {
            e.printStackTrace();
        }
        System.out.println("RocketMQConsumer Started! group=" + consumer.getConsumerGroup() + " instance=" + consumer.getInstanceName()
        );
    }
}
