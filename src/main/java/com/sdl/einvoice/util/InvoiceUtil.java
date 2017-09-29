package com.sdl.einvoice.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * 工具类
 * @Author majingyuan
 * @Date Create in 2017/9/26 15:35
 */
public class InvoiceUtil {

    /**
     * 获取流水好
     * @return
     */
    public static String getSerialNo(){
        String serialNo = UUID.randomUUID().toString();
        return serialNo;
    }

    /**
     * 获取发送时间
     * @return
     */
    public static String getPostTime(){
        String postTime;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        postTime = sdf.format(new Date());
        return postTime;
    }
}
