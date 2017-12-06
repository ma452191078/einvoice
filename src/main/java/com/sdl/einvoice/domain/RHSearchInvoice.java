package com.sdl.einvoice.domain;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 瑞宏接口-查询发票
 * @author majingyuan
 * Created by majingyuan on 2017/9/26.
 */
@Data
public class RHSearchInvoice {
//    操作流水号
    private String serialNo;
//    请求发送时间yyyy-MM-dd HH:mm:ss
    private String postTime;
    private List<Criteria> criteria;
}
