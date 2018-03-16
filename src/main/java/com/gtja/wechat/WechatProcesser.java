package com.gtja.wechat;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class WechatProcesser implements PageProcessor {
    //"Mozilla/5.0 (Windows NT 10.0; Win64; x64)" +" AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36";
    private final String USER_AGENT = "Chrome/64.0.3282.186";

    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);

    //用来存储cookie信息
    private static Set<Cookie> cookies;

    public void login()
    {
        System.setProperty("webdriver.chrome.driver", "C:/bin/chromedriver.exe");
        WebDriver dr =  new ChromeDriver();
        dr.get("http://weixin.sogou.com/");
        dr.findElement(By.id("loginBtn")).click();
        dr.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

        try {
            Thread.sleep(50000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        dr.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        dr.get("http://weixin.sogou.com/");
        cookies = dr.manage().getCookies();
        for (Cookie cookie : cookies) {
            System.out.println(cookie.getName().toString()+"======================="+cookie.getValue().toString());
        }
        dr.quit();
    }

    @Override
    public void process(Page page) {
        String number = page.getHtml().xpath("//div[@id='pagebar_container']//span/text()").toString();
        System.out.println(number);
        if(number!=null){
            Integer nextNumber = Integer.parseInt(number)+1;
            //http://weixin.sogou.com/weixin?query=%E4%B8%A4%E4%BC%9A&type=2&page=2&ie=utf8
            String nextPage = page.getHtml().xpath("//div[@id='pagebar_container']//a[@id='sogou_page_"+nextNumber+"']/@href").toString();
            String realNextPage = "http://weixin.sogou.com/weixin"+nextPage;
            page.addTargetRequest(realNextPage);
        }
        // 获取页面上的所有匹配指定正则的链接
        List<String> targetUrls = page.getHtml().links().regex("http://mp.weixin.qq.com/s\\?src=.*").all();
        page.addTargetRequests(targetUrls);
        //if (!page.getUrl().regex("http://mp.weixin.qq.com/s\\?src=.*").match()) {}
        if(page.getHtml().xpath("//h2[@id='activity-name']/text()").toString()!=null){
            page.putField("title", page.getHtml().xpath("//h2[@id='activity-name']/text()").toString());
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

    public static void main(String[] args) {
        //HttpClientDownloader downloader = new HttpClientDownloader();
        //downloader.setProxyProvider(SimpleProxyProvider.from(new Proxy("10.176.163.58", 3128)));
        String keyword = "两会";
        WechatProcesser wechatProcesser = new WechatProcesser();
        wechatProcesser.login();
        Spider.create(wechatProcesser)//.setDownloader(downloader)
                .addUrl("http://weixin.sogou.com/weixin?type=2&ie=utf8&query="+keyword)
                .thread(5)
                .run();
    }
}
