package com.gtja.processer;

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

public class SinaProcessor implements PageProcessor {
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);
    private final String USER_AGENT = "Chrome/64.0.3282.186";
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
        //http://finance.sina.com.cn/money/forex/forexinfo/2018-06-27/doc-ihencxtu9541395.shtml
        //http://news.sina.com.cn/c/2018-06-26/doc-ihencxtu6558276.shtml
        //xpath('//div[contains(@class,"a")]')   #它会取得所有class为a的元素
        //xpath('//div[contains(@class,"a") and contains(@class,"b")]') #它会取class同时有a和b的元素
        List<String> targetUrls = page.getHtml().xpath("//div[contains(@class,\"r-info2\")]//h2/a/@href").all();
        page.addTargetRequests(targetUrls);
        //将下一页加入列表
        String currentUrl = page.getUrl().toString();
        int index = currentUrl.indexOf("&page=");
        int number = index+6;
        int thisPage = Character.getNumericValue(currentUrl.charAt(number));
        int nextPage = thisPage+1;
        System.out.println("下一页"+nextPage);
        if (nextPage < 10){
            String nextUrl = currentUrl.replace("&page="+thisPage,"&page="+nextPage);
            page.addTargetRequest(nextUrl);
        }
    }

    //获取url
    public static String getUrl(String keyword){
        System.setProperty("webdriver.chrome.driver", "C:/bin/chromedriver.exe");
        WebDriver driver =  new ChromeDriver();
        driver.get("http://search.sina.com.cn/");
        // 等待加载完成
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        // 获取页面元素
        WebElement elemUsername = driver.findElement(By.xpath("//div[@id=\"tabc02\"]/form/div/input[@name='q']"));
        WebElement btn = driver.findElement(By.xpath("//div[@id=\"tabc02\"]/form/div/input[@class='ipt-submit']"));
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
        System.out.println(currentUrl);
        WebElement url = driver.findElement(By.xpath("//div[@id=\"_function_code_page\"]/a[1]"));
        url.click();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String realUrl = driver.getCurrentUrl();
        System.out.println(driver.getCurrentUrl());
        //http://so.hexun.com/default.do?type=stock&key=%C1%BD%BB%E1
        //http://so.hexun.com/list.do?type=ALL&stype=ARTICLE&key=%C1%BD%BB%E1&page=1
        driver.quit();
        return realUrl;
    }

    //获取页码
    public static String getNextUrl(){
        return "";
    }

    @Override
    public Site getSite() {
        return site != null ? site : Site.me().setSleepTime(1000).setCycleRetryTimes(3)
                .setRetrySleepTime(1000).setUserAgent(USER_AGENT).setTimeOut(10000)
                .addHeader("Accept-Encoding", "gzip,dflate,br");
    }

    public static void main(String[] args) {
        //http://search.sina.com.cn/?q=%C1%BD%BB%E1&c=news&from=index&col=&range=&source=&country=&size=&time=&a=&page=2&pf=2131425441&ps=2134309112&dpc=1
        String currentUrl = SinaProcessor.getUrl("两会");
        int index = currentUrl.indexOf("&page=2");
        System.out.println(index);
        System.out.println(currentUrl.charAt(index+6));
        String firstUrl = currentUrl.replace("&page=2","&page=1");
        System.out.println(firstUrl);
        SinaProcessor sinaProcessor = new SinaProcessor();
        Spider spider = Spider.create(sinaProcessor);
        spider.addUrl(firstUrl).thread(5).run();


        Iterator iter = hashMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            System.out.println(key + ":" + value);
        }
    }
}
