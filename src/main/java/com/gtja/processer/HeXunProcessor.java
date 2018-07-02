package com.gtja.processer;

import com.gtja.dao.SpotDao;
import com.gtja.util.StockNameUtil;
import com.gtja.util.StockUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
/*
和讯网
 */
public class HeXunProcessor implements PageProcessor {
    private final String USER_AGENT = "Chrome/61.0.3163.100";

    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);

    private static HashMap<String,Integer> hashMap = new HashMap();
    @Override
    public void process(Page page) {
        //匹配股票信息
        List<String> code_stock = page.getHtml().regex("((002|000|001|300|600|601|603)\\d{3})").all();
        //根据股票名称匹配，后续从
        List<String> name_stock = page.getHtml().regex(StockNameUtil.getRegexByName()).all();
        StockUtil.addToMap(code_stock,hashMap);
        StockUtil.addToMap(name_stock,hashMap);
        //将文章详情页放入待爬取队列
        //http://caidao.hexun.com/19988020/article72801.html
        List<String> targetUrls = page.getHtml().links().regex("http://caidao.hexun.com/[0-9]*/article[0-9]*.html").all();
        page.addTargetRequests(targetUrls);
        String currentUrl = page.getUrl().toString();
        System.out.println("当前地址"+currentUrl);
        if (page.getUrl().regex("http://so.hexun.com/list.do\\?type=ALL&stype=ARTICLE&key=.*").match()){
            Integer nextPage = Integer.parseInt(currentUrl.split("=")[4])+1;
            String nextUrl = currentUrl.replace(currentUrl.split("=")[4],nextPage+"");
            page.addTargetRequest(nextUrl);
        }
    }

    public static String getUrl(String keyword){
        System.setProperty("webdriver.chrome.driver", "C:/bin/chromedriver.exe");
        WebDriver driver =  new ChromeDriver();
        driver.get("http://so.hexun.com/default.do?type=stock");
        // 等待加载完成
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        // 获取页面元素
        WebElement elemUsername = driver.findElement(By.id("key"));
        WebElement btn = driver.findElement(By.id("btnSearch"));
        // 操作页面元素
        elemUsername.clear();
        elemUsername.sendKeys(keyword);
        btn.click();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String currentUrl = driver.getCurrentUrl();
        //http://so.hexun.com/default.do?type=stock&key=%C1%BD%BB%E1
        //http://so.hexun.com/list.do?type=ALL&stype=ARTICLE&key=%C1%BD%BB%E1&page=1
        driver.quit();
        String token = currentUrl.split("=")[2];
        return "http://so.hexun.com/list.do?type=ALL&stype=ARTICLE&key="+token+"&page=1";
    }

    @Override
    public Site getSite() {
        return site != null ? site : Site.me().setSleepTime(1000).setCycleRetryTimes(3)
                .setRetrySleepTime(1000).setUserAgent(USER_AGENT).setTimeOut(10000)
                .addHeader("Accept-Encoding", "gzip,dflate,br");
    }

    public static void main(String[] args) {
        Spider spider = Spider.create(new HeXunProcessor());
        /*List<String> urls = SpotDao.spotUrl();
        for (String s: urls){
            spider.addUrl(getUrl(s)
                    .replace("default.do?type=stock","list.do?type=STOCK&stype=ARTICLE")
                    +"&page=1");
        }*/
        spider.addUrl(getUrl("两会")).thread(5).run();
        Iterator iter = hashMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            System.out.println(key + ":" + value);
        }
    }
}
