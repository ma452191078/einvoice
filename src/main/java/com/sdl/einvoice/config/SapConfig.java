package com.sdl.einvoice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * 发票配置信息
 * @author majingyuan
 */

@Component
@ConfigurationProperties(prefix = "sapconfig")
@EnableConfigurationProperties
@Data
public class SapConfig {

    private String ashost;
    private String sysnr;
    private String client;
    private String user;
    private String passwd;
    private String lang;
    //最大空连接数
    private String poolCapacity;
    //最大活动连接数
    private String peakLimit;
}
