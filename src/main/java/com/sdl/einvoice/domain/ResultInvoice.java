package com.sdl.einvoice.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 报文发票
 * @author majingyuan
 * @date Create in 2017/9/27 08:40
 */
@Data
public class ResultInvoice {
    private String ordreNo;
    private String taxpayerName;
    //    公司纳税人识别号
    private String taxpayerCode;
    //    公司地址
    private String taxpayerAddress;
    //    公司对公电话
    private String taxpayerTel;
    //    公司对公银行名称
    private String taxpayerBankName;
    //    公司对公账户
    private String taxpayerBankAccount;
    //    经销商名称
    private String customerName;
    //    经销商纳税人识别号
    private String customerCode;
    //    经销商地址
    private String customerAddress;
    //    经销商电话
    private String customerTel;
    //    经销商对公银行名称
    private String customerBankName;
    //    经销商对公账户
    private String customerBankAccount;
    //    发票代码+发票号码
    private String code;
    //    校验码
    private String checkCode;
    //    税控码
    private String fiscalCode;
    //    发票状态
    private String status;
    //    开票日期
    private String generateTime;
    //    价税合计
    private Number totalAmount;
    //    不含税金额
    private Number noTaxAmount;
    //    税额
    private Number taxAmount;
    //    开票人
    private String drawer;
    //    收款人
    private String payee;
    //    复核人
    private String reviewer;
    //    备注
    private String remark;
    //    PDF文件下载地址
    private String pdfUnsignedUrl;
    //    发票查看地址
    private String viewUrl;
    //    关联发票代码+号码
    private String relatedCode;
    //    冲红原因
    private String validReason;
    //    冲红时间
    private String validTime;
    //    发票明细
    private List<ResultInvoiceItem> items;
    //    扩展参数
    private Map<String, Object> extendedParams;
}
