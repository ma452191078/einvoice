package com.sdl.einvoice.util;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

/**
 * 网络请求工具类
 * @Author majingyuan
 * @Date Create in 2017/9/27 08:11
 */
public class HttpUtil {

    private static class DefaultTrustManager implements X509TrustManager {

        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public void checkClientTrusted(X509Certificate[] cert, String oauthType)
                throws java.security.cert.CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] cert, String oauthType)
                throws java.security.cert.CertificateException {
        }
    }
    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url
     *            发送请求的 URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";

        try {
            URL realUrl = new URL(url);

            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(new KeyManager[0], new TrustManager[] { new DefaultTrustManager() },
                    new SecureRandom());

            HttpsURLConnection connHttps = (HttpsURLConnection) realUrl.openConnection();
            connHttps.setSSLSocketFactory(ctx.getSocketFactory());
            connHttps.setHostnameVerifier(new HostnameVerifier() {

                public boolean verify(String hostname, SSLSession session) {
                    return true;// 默认都认证通过
                }
            });
            HttpURLConnection httpConn = connHttps;
            // 打开和URL之间的连接
//            HttpURLConnection httpConn = (HttpURLConnection) realUrl.openConnection();
            // //设置连接属性
            httpConn.setDoOutput(true);// 使用 URL 连接进行输出
            httpConn.setDoInput(true);// 使用 URL 连接进行输入
            httpConn.setUseCaches(false);// 忽略缓存
            httpConn.setRequestMethod("POST");// 设置URL请求方法

            // 设置请求属性
            // 获得数据字节数据，请求数据流的编码，必须和下面服务器端处理请求流的编码一致
            httpConn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            httpConn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接

            // 建立输出流，并写入数据
            OutputStream outputStream = httpConn.getOutputStream();
            outputStream.write(param.getBytes());
            outputStream.flush();
            // 获得响应状态
            int responseCode = httpConn.getResponseCode();

            if (HttpURLConnection.HTTP_OK == responseCode) {// 连接成功
                // 当正确响应时处理数据
                StringBuffer sb = new StringBuffer();
                String readLine;
                BufferedReader responseReader;
                // 处理响应流，必须与服务器响应流输出的编码一致
                responseReader = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "UTF-8"));
                while ((readLine = responseReader.readLine()) != null) {
                    sb.append(readLine).append("\n");
                }
                responseReader.close();
                //tv.setText(sb.toString());
                result = sb.toString();
                //System.out.println(sb.toString());
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }



        return result;
    }
}
