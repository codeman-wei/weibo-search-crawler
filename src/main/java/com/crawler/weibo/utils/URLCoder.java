package com.crawler.weibo.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class URLCoder {
    public static String URLEncoderGBK(String str){
        String result = "";
        if (null == str) {
            return "";
        }
        try {
            result = URLEncoder.encode(str, "gb2312");
        } catch (UnsupportedEncodingException e){
            e.printStackTrace();
            return "";
        }
        return result;
    }
    public static String URLDecoderGBK(String str){
        String result ="";
        if(null == str){
            return "";
        }
        try {
            result = URLDecoder.decode(str, "gb2312");
        } catch (UnsupportedEncodingException e){
            e.printStackTrace();
            return "";
        }
        return result;
    }
    public static String URLEncoderUTF(String str){
        String result = "";
        if (null == str) {
            return "";
        }
        try {
            result = URLEncoder.encode(str, "utf-8");
        } catch (UnsupportedEncodingException e){
            e.printStackTrace();
            return "";
        }
        return result;
    }
    public static String URLDecoderUTF(String str){
        String result ="";
        if(null == str){
            return "";
        }
        try {
            result = URLDecoder.decode(str, "utf-8");
        } catch (UnsupportedEncodingException e){
            e.printStackTrace();
            return "";
        }
        return result;
    }
}
