package com.sdl.einvoice.domain;

import lombok.Data;
import org.omg.CORBA.PRIVATE_MEMBER;

import java.util.List;

/**
 * 瑞宏接口-发票类
 * Created by majingyuan on 2017/9/26.
 */
@Data
public class RHInvoice {

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
