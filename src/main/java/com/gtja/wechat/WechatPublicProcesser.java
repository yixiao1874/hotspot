package com.gtja.wechat;

import org.apache.log4j.DailyRollingFileAppender;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.JsonPathSelector;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class WechatPublicProcesser implements PageProcessor {
    private final String USER_AGENT = "Chrome/64.0.3282.186";

    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);

    //用来存储cookie信息
    private static Set<Cookie> cookies;
    private static String token;
    @Override
    public void process(Page page) {
        List<String> url = new JsonPathSelector("$.app_msg_list[*].link").selectList(page.getRawText());
        for(String s:url){
            System.out.println("**URL**"+s);
        }
    }

    @Override
    public Site getSite() {
        for (Cookie cookie : cookies) {
            System.out.println(cookie.getName().toString()+"======================="+cookie.getValue().toString());
            site.addCookie(cookie.getName().toString(),cookie.getValue().toString());
        }
        return site != null ? site : Site.me().setSleepTime(1000).setCycleRetryTimes(3)
                .setRetrySleepTime(1000).setUserAgent(USER_AGENT).setTimeOut(10000)
                .addHeader("Accept-Encoding", "gzip,dflate,br");
    }

    public void login()
    {

        //driver.switchTo().frame("contentFrame");
        System.setProperty("webdriver.chrome.driver", "C:/bin/chromedriver.exe");
        WebDriver dr =  new ChromeDriver();
        dr.get("https://mp.weixin.qq.com/");
        // 等待加载完成
        dr.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        cookies = dr.manage().getCookies();
        for (Cookie cookie : cookies) {
            System.out.println(cookie.getName().toString()+"======================="+cookie.getValue().toString());
        }
        // 获取页面元素
        WebElement elemUsername = dr.findElement(By.name("account"));
        WebElement elemPassword = dr.findElement(By.name("password"));
        WebElement btn = dr.findElement(By.className("btn_login"));
        //WebElement rememberMe = dr.findElement(By.className("frm_checkbox"));
        // 操作页面元素
        elemUsername.clear();
        elemPassword.clear();
        elemUsername.sendKeys("*************@qq.com");
        elemPassword.sendKeys("***********");
        //rememberMe.click();
        btn.click();

        try {
            Thread.sleep(50000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("denglu -----------");
        dr.get("https://mp.weixin.qq.com/");
        //微信公众平台
        //String token = dr.getTitle();
        //https://mp.weixin.qq.com/cgi-bin/home?t=home/index&lang=zh_CN&token=74821491
        String tokenUrl = dr.getCurrentUrl();
        token = tokenUrl.substring(tokenUrl.lastIndexOf("=")+1);
        //CDwindow-F3C3D9E135264598203633F4F9AA748A
        //String token3 = dr.getWindowHandle();
        //System.out.println("获取的token为"+"token1"+token+"###token2"+token2+"***token3"+token3);
        cookies = dr.manage().getCookies();
        for (Cookie cookie : cookies) {
            System.out.println(cookie.getName().toString()+"======================="+cookie.getValue().toString());
        }


        dr.quit();
    }

    public static void main(String[] args) {
        WechatPublicProcesser wechatPublicProcesser = new WechatPublicProcesser();
        wechatPublicProcesser.login();
        /*Spider.create(wechatPublicProcesser)//.setDownloader(downloader)
                .addUrl("https://mp.weixin.qq.com/cgi-bin/appmsg?" +
                        "token="+token+"&lang=zh_CN&f=json&ajax=1&random=0.9915912628321586" +
                        "&action=list_ex&begin=0&count=5&query=&fakeid=MjM5MDQ4MzU5NQ%3D%3D&type=9")
                .thread(5)
                .run();*/
    }
}
