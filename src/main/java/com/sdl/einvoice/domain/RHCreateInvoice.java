package com.sdl.einvoice.domain;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 瑞宏接口-新开发票
 * Created by majingyuan on 2017/9/26.
 */
@Data
public class RHCreateInvoice {
//    流水号
    private String serialNo;
//    请求时间
    private String postTime;
//    订单
    private RHOrder order;
//    发票
    private RHInvoice invoice;
//    通知方式
    private List<RHNotices> notices;

    private Map<String, Object> extendedParams;
}
