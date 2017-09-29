package com.sdl.einvoice.domain;

import lombok.Data;

/**
 * 瑞宏接口-发票明细
 * Created by majingyuan on 2017/9/26.
 */
@Data
public class RHInvoiceItem {

//    发票性质，0正常行，1折扣行，2被折扣行
    private String type;
//    商品编码
    private String code;
    /**
     * 商品名称，折扣行与被折扣行商品名称一致，
     * 金额和税额以负数填写，税率与被折扣行商品税率相同，
     * 其他栏不填写
     */
    private String name;
    //规格型号
    private String spec;
//    商品单价，必须等于amount/quantity的四舍五入值
    private Number price;
//    数量，必须大于0.000001
    private Number quantity;
//    单位
    private String uom;
//    税率，只能为0或0.03或0.04或0.06或0.11或0.13或0.17
    private Number taxRate;
//    价税合计金额
    private Number amount;
//    商品分类编码，复合肥税收编码为1070204070000000000，有机肥税收编码为1070205010000000000
    private String catalogCode;
//    优惠政策标识，0不使用，1使用
    private String preferentialPolicyFlg;
//    增值税特殊管理
    private String addedValueTaxFlg;
//    零税率标识，
    private String zeroTaxRateFlg;
}
