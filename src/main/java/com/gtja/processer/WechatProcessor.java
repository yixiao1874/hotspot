package com.gtja.processer;

import com.gtja.dao.SpotDao;
import com.gtja.dao.StockDao;
import com.gtja.util.StockNameUtil;
import com.gtja.util.StockUtil;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.*;
/*
搜狗微信公众号文章搜索
 */
public class WechatProcessor implements PageProcessor {
    StockDao stockDao = new StockDao();
    //"Mozilla/5.0 (Windows NT 10.0; Win64; x64)" +" AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36";
    private final String USER_AGENT = "Chrome/64.0.3282.186";

    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);

    private static HashMap<String,Integer> hashMap = new HashMap();

    @Override
    public void process(Page page) {
        //匹配股票信息
        List<String> code_stock = page.getHtml().regex("((002|000|300|600|601|603)\\d{3})").all();
        //根据股票名称匹配，后续从
        List<String> name_stock = page.getHtml().regex(StockNameUtil.getRegexByName()).all();
        StockUtil.addToMap(code_stock,hashMap);
        StockUtil.addToMap(name_stock,hashMap);
        String number = page.getHtml().xpath("//div[@id='pagebar_container']//span/text()").toString();
        System.out.println(number);
        if(number!=null&&Integer.parseInt(number)<10){
            Integer nextNumber = Integer.parseInt(number)+1;
            //http://weixin.sogou.com/weixin?query=%E4%B8%A4%E4%BC%9A&type=2&page=2&ie=utf8
            String nextPage = page.getHtml().xpath("//div[@id='pagebar_container']//a[@id='sogou_page_"+nextNumber+"']/@href").toString();
            String realNextPage = "http://weixin.sogou.com/weixin"+nextPage;
            page.addTargetRequest(realNextPage);
        }
        // 获取页面上的所有匹配指定正则的链接
        List<String> targetUrls = page.getHtml().links().regex("http://mp.weixin.qq.com/s\\?src=.*").all();
        if(targetUrls.size()!=0){
            page.addTargetRequests(targetUrls);
        }
    }

    @Override
    public Site getSite() {
        /*for (Cookie cookie : cookies) {
            System.out.println(cookie.getName().toString()+"======================="+cookie.getValue().toString());
            site.addCookie(cookie.getName().toString(),cookie.getValue().toString());
        }*/
        return site != null ? site : Site.me().setSleepTime(1000).setCycleRetryTimes(3)
                .setRetrySleepTime(1000).setUserAgent(USER_AGENT).setTimeOut(10000)
                .addHeader("Accept-Encoding", "gzip,dflate,br");
    }

    public static void main(String[] args) {
        Spider spider = Spider.create(new WechatProcessor());
        List<String> urls = SpotDao.spotUrl();
        for (String s: urls){
            spider.addUrl(s);
        }
        spider.thread(5).run();
        spider.close();
        Spider spider2 = Spider.create(new GuanChaProcessor());
        spider2.addUrl("http://www.guancha.cn/Search/?k=中美贸易战&y=1&ps=20&pi=3").thread(5).run();
        System.out.println("+++++++++++++closr++++++++++++++++");
        spider2.close();
        System.out.println("+++++++++++++closr++++++++++++++++");
        Iterator iter = hashMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            System.out.println(key + ":" + value);
        }
    }
}
