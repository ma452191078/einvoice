package com.sdl.einvoice.constant;

/**
 * 发票静态变量
 */
public interface InvoiceConstant {

//    复合肥
    String catalogCodeFHF = "1070204070000000000";
//    有机肥
    String catalogCodeYJF = "1070205010000000000";

    String PRD_CREATE_URL = "https://www.chinaeinv.com/igs/api/invoiceApi.jspa?appCode=APPCODE&cmdName=CMDNAME&sign=SIGN";
    String DEV_CREATE_URL = "https://www.chinaeinv.com:943/igs/api/invoiceApi.jspa?appCode=APPCODE&cmdName=CMDNAME&sign=SIGN";

    String APPCODE = "APPCODE";
    String CMDNAME = "CMDNAME";
    String SIGN = "SIGN";

    String CODE_SUCCESS = "0";


}
