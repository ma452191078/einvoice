package com.sdl.einvoice.domain;

import lombok.Data;

/**
 * 瑞宏接口-订单类
 * Created by majingyuan on 2017/9/26.
 */
@Data
public class RHOrder {

//    订单编号
    private String orderNo;
//    消费者用户名
    private String account;
//    货物配送地址
    private String address;
//    消费者电邮地址
    private String email;
//    消费者联系电话
    private String tel;
}
