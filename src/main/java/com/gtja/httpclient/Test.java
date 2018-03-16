package com.gtja.httpclient;

public class Test {
    public static void main(String[] args) {
        String url = "https://mp.weixin.qq.com/cgi-bin/home?t=home/index&lang=zh_CN&token=74821491";
        System.out.println(url.substring(url.lastIndexOf("=")+1));
    }
}
