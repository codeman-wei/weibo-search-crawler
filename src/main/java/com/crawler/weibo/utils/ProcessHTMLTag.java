package com.crawler.weibo.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProcessHTMLTag {
    public static String delHTMLTag(String htmlStr){
        String regEx_html="<[.[^<]]*>"; //定义HTML标签的正则表达式
        Pattern p_html=Pattern.compile(regEx_html,Pattern.CASE_INSENSITIVE);
        Matcher m_html=p_html.matcher(htmlStr);
        htmlStr=m_html.replaceAll(""); //过滤html标签
        return htmlStr.trim(); //返回文本字符串
    }
    public static String getContentUrl(String htmlStr){
        String regEx_html="<a[.[^<]]*>全文"; //定义HTML标签的正则表达式
        Pattern p_html=Pattern.compile(regEx_html);

        Matcher m_html=p_html.matcher(htmlStr);
        String urlList = "";
        if(m_html.find()){
            urlList = m_html.group();
        }

//		System.out.println(urlList);
//		System.out.println( m_html.group(1) );	//匹配到的就输出
        return urlList; //返回文本字符串
    }
}
