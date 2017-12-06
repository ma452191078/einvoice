package com.sdl.einvoice.constant;

/**
 * 发票静态变量
 * @author majingyuan
 */
public interface InvoiceConstant {

    /**
     * 复合肥
     */
    String catalogCodeFHF = "1070204070000000000";
    /**
     * 有机肥
     */
    String catalogCodeYJF = "1070205010000000000";

    /**
     * 正式
     */
//    String PRD_CREATE_URL = "https://www.chinaeinv.com/igs/api/invoiceApi.jspa";
    /**
     * 测试
     */
    String PRD_CREATE_URL = "http://www.chinaeinv.com:980/igs/api/invoiceApi.jspa";

    String APPCODE = "APPCODE";
    String CMDNAME = "CMDNAME";
    String SIGN = "SIGN";

    String CODE_SUCCESS = "0";

    String CMD_CREATE = "chinaeinv.api.invoice.v3.kp_async";
    String CMD_RED = "chinaeinv.api.invoice.v3.ch_async";
    String CMD_SEARCH = "chinaeinv.api.invoice.v3.cx";

    String SEARCH_TYPE_INVOICE_CODE = "invoiceCode";
    String SEARCH_TYPE_ORDER_NO = "orderNo";
    String SEARCH_TYPE_SERIAL_NO = "singlesSerialNo";


}
