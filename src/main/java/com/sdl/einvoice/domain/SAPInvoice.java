package com.sdl.einvoice.domain;

import lombok.Data;

import java.util.List;

/**
 * SAP请求发票
 * @Author majingyuan
 * @Date Create in 2017/9/28 15:45
 */
@Data
public class SAPInvoice {
    //    流水号
    private String serialNo;
    //    请求类型，0新增发票，1红冲发票，3回调
    private String actionType;
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
    //    开票人
    private String drawer;
    //    收款人
    private String payee;
    //    复核人
    private String reviewer;
    //    税价合计
    private Number totalAmount;
    //    备注
    private String remark;
    //    发票明细
    private List<RHInvoiceItem> items;
}
