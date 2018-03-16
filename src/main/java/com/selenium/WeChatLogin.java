package com.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public class WeChatLogin {

    private static Set<Cookie> cookies;

    public static void main(String[] args) throws Exception{
        System.setProperty("webdriver.chrome.driver", "C:/bin/chromedriver.exe");
        WebDriver dr =  new ChromeDriver();
        dr.get("http://weixin.sogou.com/");
        dr.findElement(By.id("loginBtn")).click();
        dr.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        cookies = dr.manage().getCookies();

        for (Cookie cookie : cookies) {
            System.out.println(cookie.getName().toString()+"======================="+cookie.getValue().toString());
        }

        Thread.sleep(50000);
        dr.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        dr.get("http://weixin.sogou.com/");
        cookies = dr.manage().getCookies();
        for (Cookie cookie : cookies) {
            System.out.println(cookie.getName().toString()+"======================="+cookie.getValue().toString());
        }


        dr.quit();

    }


}
