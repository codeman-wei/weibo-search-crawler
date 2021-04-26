package com.crawler.weibo.utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import java.util.HashMap;
import java.util.Map;

public class GetMethod {
    public static String httpGetMethod(String url, Map<String, String>headerMap, String coding) {
        HttpGet httpGet = new HttpGet(url);
        for(Map.Entry<String, String> headerEntity : headerMap.entrySet()) {
            httpGet.setHeader(headerEntity.getKey(), headerEntity.getValue());
//			System.out.println(headerEntity.getKey()+":"+ headerEntity.getValue());
        }
        GetHttpclient.initPools();
        HttpClient httpClient = GetHttpclient.getHttpClient();
        HttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            int status = response.getStatusLine().getStatusCode();
            if(status == 200) {

                return EntityUtils.toString(response.getEntity(),coding);
            }
            else {
                System.out.println("爬取"+url+"异常，状态码为："+status);
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return "-1";
    }
    public static void main(String[] args) {
        String url = "https://www.amac.org.cn//portal/front/pri/organ/findNoRegOrganPage?pageSize=10&organName=";
        Map<String, String> map = new HashMap<String, String>();
        map.put("Accept", "application/json, text/javascript, */*; q=0.01");
        map.put("Accept-Encoding", "gzip, deflate, br");
        map.put("Accept-Language", "zh-CN,zh;q=0.9");
        map.put("Connection", "keep-alive");
//		map.put("Content-Type", "application/json");
        map.put("Host", "zhidao.baidu.com");
//		map.put("Referer", "https://zhidao.baidu.com/search?word=%C3%C0%B9%FA&ie=gbk&site=-1&sites=0&date=0&pn=0");
//		map.put("Referer", "https://zhidao.baidu.com/search?lm=0&rn=10&pn=0&fr=search&ie=gbk&word=%C3%C0%B9%FA");
        map.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.111 Safari/537.36");
//		map.put("Upgrade-Insecure-Requests", "1");

//		String result = httpGetMethod(url, map, "");
//		System.out.println(result);
//		String urlTest = "https://zhidao.baidu.com/search?lm=0&rn=10&pn=10&fr=search&ie=gbk&word=%c3%c0%b9%fa%b4%f3%d1%a1&date=2";
        String urlTest = "https://zhidao.baidu.com/search?lm=0&site=-1&sites=0&ie=gbk&word=%C3%C0%B9%FA%B4%F3%D1%A1&date=2";
        String result = httpGetMethod(urlTest, map, "");
        System.out.println(result);
        if(result == "") {
            System.out.println("retry");
            result = httpGetMethod(urlTest, map, "");
            System.out.println(result);
        }
    }

}
