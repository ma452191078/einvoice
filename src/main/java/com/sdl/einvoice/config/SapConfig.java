package com.sdl.einvoice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 发票配置信息
 */
@Data
@Component
@ConfigurationProperties(prefix = "sapconfig")
public class SapConfig {

    private String ashost;
    private String sysnr;
    private String client;
    private String user;
    private String passwd;
    private String lang;
}
