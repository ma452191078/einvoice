package com.sdl.einvoice.domain;

import lombok.Data;

/**
 * 异步响应报文
 * @Author majingyuan
 * @Date Create in 2017/9/26 17:02
 */
@Data
public class AsyncResult {

//    操作流水号
    private String serialNo;
//    发送时间
    private String postTime;
//    处理结果代码
    private String code;
//    处理结果消息
    private String message;
}
