package com.sdl.einvoice.domain;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 瑞宏接口-冲红发票
 * Created by majingyuan on 2017/9/26.
 */
@Data
public class RHRedInvoice {
//    操作流水号
    private String serialNo;
//    请求发送时间yyyy-MM-dd HH:mm:ss
    private String postTime;
//    原发票代码+原发票号码
    private String originalCode;
//    冲红原因
    private String reason;
//    发票项目
    private List<RHInvoiceItem> items;
//    private RHNotices notices;
//    private Map<String, Object> extendedParams;
}
