package com.sdl.einvoice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 消息队列配置文件
 * @author majingyuan
 * @date Create in 2017/12/1 16:51
 */

@Data
@Component
@ConfigurationProperties(prefix = "mqconfig")
public class MQConfig {

    /**
     * 消息服务器地址及端口
     */
    private String nameSrvAddr;
    /**
     * 消费者分组名
     */
    private String consumerGroupName;
    /**
     * 生产者分组名
     */
    private String producerGroupName;
    /**
     * 标签
     */
    private String topics;
}
