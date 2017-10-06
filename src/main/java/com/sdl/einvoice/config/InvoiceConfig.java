package com.sdl.einvoice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 发票配置信息
 */
@Data
@Component
@ConfigurationProperties(prefix = "invoice")
public class InvoiceConfig {

    /**
     * 公司纳税人识别号
     */
    private String taxpayerCode;
    /**
     * 地址
     */
    private String taxpayerAddress;
    /**
     * 对公电话
     */
    private String taxpayerTel;
    /**
     * 银行名称
     */
    private String taxpayerBankName;
    /**
     * 对公账号
     */
    private String taxpayerBankAccount;

    // 由电子发票平台分配的appCode
    private String appCode;

    private String cmdName;
    // 签名
    private String sign;
    // 证书路径
    private String keyStorePath;
    // 证书别名
    private String keyStoreAbner;
    // 证书密码
    private String keyStorePassWord;



}
