package com.sdl.einvoice.domain;

import lombok.Data;

import java.util.List;

/**
 * SAP异步通知
 * @author majingyuan
 * @date Create in 2017/9/28 15:45
 */
@Data
public class SAPNotify {
    //    流水号
    private String serialNo;
    //    处理结果
    private String code;
    // 处理结果消息
    private String message;
    // 销售和分销凭证号
    private String orderNo;
    // 原发票代码+发票号码
    private String oriCode;
    // 发票状态，1正常，3红冲，4被红冲
    private String status;
    // 开票日期
    private String gentime;
    // pdf下载地址
    private String pdfUrl;
    // 发票查看地址
    private String viewUrl;
    // 关联发票
    private String relatedCode;
    // 冲红原因
    private String validReason;
    // 冲红时间
    private String validTime;

    /**
     * 税价合计金额
     */
    private Number tAmount;
    /**
     * 不含税金额
     */
    private Number noTax;
    /**
     * 税额
     */
    private Number taxAmount;
}
