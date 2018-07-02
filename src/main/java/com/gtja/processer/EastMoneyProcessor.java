package com.gtja.processer;

import com.gtja.dao.SpotDao;
import com.gtja.util.StockNameUtil;
import com.gtja.util.StockUtil;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.JsonPathSelector;

import java.util.*;
import java.util.concurrent.TimeUnit;
/*
东方财富网
 */
public class EastMoneyProcessor implements PageProcessor{
    //Mozilla/5.0 (Windows NT 10.0; Win64; x64)" +" AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36
    private final String USER_AGENT = "Chrome/61.0.3163.100";

    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);

    private static HashMap<String,Integer> hashMap = new HashMap();

    private static Set<Cookie> cookies;
    @Override
    public void process(Page page) {
        //匹配股票信息
        List<String> code_stock = page.getHtml().regex("((002|000|001|300|600|601|603)\\d{3})").all();
        //根据股票名称匹配，后续从
        List<String> name_stock = page.getHtml().regex(StockNameUtil.getRegexByName()).all();
        StockUtil.addToMap(code_stock,hashMap);
        StockUtil.addToMap(name_stock,hashMap);
        //将下一页加入爬取队列   http://so.eastmoney.com/Web/GetSearchList?type=20&pageindex=1&pagesize=10&keyword=%E4%B8%A4%E4%BC%9A
        //限制抓10页
        if (page.getUrl().regex("http://so.eastmoney.com/Web/GetSearchList\\?type=20&pageindex=.*").match()
                &&Integer.parseInt(page.getUrl().toString().split("&")[1].substring(page.getUrl().toString().split("&")[1].lastIndexOf("=")+1))<11) {
            String currentUrl = page.getUrl().toString();
            String pageIndex = currentUrl.split("&")[1];
            Integer pageNumber = Integer.parseInt(pageIndex.substring(pageIndex.lastIndexOf("=")+1));
            System.out.println(pageNumber+"++++++++++++++++++++=====================");
            Integer nextPage = pageNumber+1;
            page.addTargetRequest(currentUrl.replace("pageindex="+pageNumber,"pageindex="+nextPage));
            List<String> news = new JsonPathSelector("$.Data[*].Art_Url").selectList(page.getRawText());
            page.addTargetRequests(news);
        }
    }


    public void getCookie(){
        System.setProperty("webdriver.chrome.driver", "/usr/local/chromedriver");
        ChromeOptions chromeOptions = new ChromeOptions();
        //设置为 headless 模式 （必须）
        chromeOptions.addArguments("headless");
        chromeOptions.addArguments("disable-extensions");
        chromeOptions.addArguments("disable-gpu");
        chromeOptions.addArguments("no-sandbox");
        WebDriver dr =  new ChromeDriver(chromeOptions);
        dr.get("http://finance.eastmoney.com/yaowen.html");
        // 等待加载完成
        dr.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        cookies = dr.manage().getCookies();
        if(cookies == null){
            System.out.println("===================cuiowu=================");
        }else {
            for (Cookie cookie : cookies) {
                System.out.println(cookie.getName().toString()+"======================="+cookie.getValue().toString());
            }
        }
        dr.quit();
    }

    @Override
    public Site getSite() {
        for (Cookie cookie : cookies) {
            System.out.println(cookie.getName().toString()+"======================="+cookie.getValue().toString());
            site.addCookie(cookie.getName().toString(),cookie.getValue().toString());
        }
        return site.setCycleRetryTimes(3).setUserAgent(USER_AGENT).setTimeOut(10000)
                .addHeader("Accept-Encoding", "gzip,dflate")
                .addHeader("Accept-Language","zh-CN,zh;q=0.9")
                .addHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
    }

    public static void main(String[] args) {
        /*Spider spider = Spider.create(new EastMoneyProcessor());
        List<String> urls = SpotDao.eastMoneyUrl();
        for (String s: urls){
            spider.addUrl(s);
        }
        spider.thread(5).run();
        Iterator iter = hashMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            System.out.println(key + ":" + value);
        }*/
        EastMoneyProcessor eastMoneyProcessor = new EastMoneyProcessor();
        eastMoneyProcessor.getCookie();
        Spider.create(eastMoneyProcessor)
                .addUrl("http://so.eastmoney.com/Web/GetSearchList?type=20&pageindex=1&pagesize=10&keyword=%E4%B8%A4%E4%BC%9A")
                .run();
        Iterator iter = hashMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            System.out.println(key + ":" + value);
        }
    }
}
