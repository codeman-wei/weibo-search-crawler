package com.crawler.weibo.utils;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

public class GetHttpclient {
    public static PoolingHttpClientConnectionManager cm = null;
    public static CloseableHttpClient httpClient = null;
    //	private int totalPage = 0;
    private static final int DEFAULT_TIME_OUT = 150000;
    private static final int count = 20;	//担心被墙设置小点
    private static final int totalCount = 40;
    private static final int Http_Default_Keep_Time = 150000;
    private static RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(DEFAULT_TIME_OUT)
            .setConnectTimeout(DEFAULT_TIME_OUT).setConnectionRequestTimeout(DEFAULT_TIME_OUT).setExpectContinueEnabled(false).build();
    public static synchronized void initPools(){
        if(httpClient == null) {

            SSLContext sslContext = null;
            try {
                sslContext = createIgnoreVerifySSL();
            } catch (KeyManagementException | NoSuchAlgorithmException e) {
                // TODO 自动生成的 catch 块
                e.printStackTrace();
            }

            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", new SSLConnectionSocketFactory(sslContext))
                    .build();
            cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            cm.setDefaultMaxPerRoute(count);
            cm.setMaxTotal(totalCount);
            httpClient = HttpClients.custom().setKeepAliveStrategy(defaultStrategy).setConnectionManager(cm)
                    .setDefaultRequestConfig(requestConfig)
                    .setRedirectStrategy(new LaxRedirectStrategy()).build();
        }
    }
    /**
     *  Http connection keepAlive 设置
     */

    public static ConnectionKeepAliveStrategy defaultStrategy = new ConnectionKeepAliveStrategy() {
        public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
            HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
            int keepTime = Http_Default_Keep_Time;
            while (it.hasNext()) {
                HeaderElement he = it.nextElement();
                String param = he.getName();
                String value = he.getValue();
                if (value != null && param.equalsIgnoreCase("timeout")) {
                    try {
                        return Long.parseLong(value) * 1000;
                    } catch (Exception e) {
                        e.printStackTrace();
//                        logger.error("format KeepAlive timeout exception, exception:" + e.toString());
                    }
                }
            }
            return keepTime;
        }
    };
    /** 完成httpClient配置 */
    public static CloseableHttpClient getHttpClient() {
        return httpClient;
    }
    public static PoolingHttpClientConnectionManager poolingHttpClientConnectionManager() {
        return cm;
    }

    /** 配置SSL */
    public static SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException,KeyManagementException{
        SSLContext sslContext = SSLContext.getInstance("SSLv3");

        X509TrustManager trustManager = new X509TrustManager() {

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                // TODO 自动生成的方法存根
                return null;
            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                                           String paramString) throws CertificateException {
                // TODO 自动生成的方法存根

            }

            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                                           String paramString) throws CertificateException {
                // TODO 自动生成的方法存根

            }
        };
        sslContext.init(null,  new TrustManager[] { trustManager }, null);
        return sslContext;
    }
}
