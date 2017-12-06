package com.sdl.einvoice.util;


import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * @author majingyuan
 * @date Create in 2017/9/26 16:29
 */
public class BASE64Util {

    /**
     * base64解码
     * @param s:需要解码的数据
     * @return 解码后的数据
     */
    public static String getStrFromBase64(String s) {
        if (s == null)
            return null;
        sun.misc.BASE64Decoder decoder = new BASE64Decoder();
        try {
            byte[] b = decoder.decodeBuffer(s);
            return new String(b, "UTF-8");
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * base64解码
     * @param s:需要解码的数据
     * @return 解码后的数据
     */
    public static byte[] getByteFromBase64(String s) {
        if (s == null)
            return null;
        sun.misc.BASE64Decoder decoder = new BASE64Decoder();
        try {
            return decoder.decodeBuffer(s);
        } catch (Exception e) {
            return null;
        }
    }
    /**
     * base64编码
     * @param s:需要编码的数据
     * @return  编码后的数据
     */
    public static String getRevFromBase64(byte[] s) {
        if (s == null)
            return null;
        sun.misc.BASE64Encoder encoder = new BASE64Encoder();
        try {
            return encoder.encode(s);
        } catch (Exception e) {
            return null;
        }
    }
}
