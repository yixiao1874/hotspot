package com.gtja.wechat;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;

import java.util.List;

public class WechatProcesser implements PageProcessor {

    private final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64)" +
            " AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36";

    private Site site;

    @Override
    public void process(Page page) {
        // 获取页面上的所有匹配指定正则的链接
        List<String> targetUrls = page.getHtml().links().regex("http://mp.weixin.qq.com/s\\?src=.*").all();
        List<String> pageUrl = page.getHtml().links().regex("\\?query=.*").all();
        page.addTargetRequests(targetUrls);
        page.addTargetRequests(pageUrl);
        page.putField("title", page.getHtml().xpath("//h2[@id='activity-name']/text()").toString());
    }

    @Override
    public Site getSite() {
        return site != null ? site : Site.me().setSleepTime(1000).setCycleRetryTimes(3)
                .setRetrySleepTime(1000).setUserAgent(USER_AGENT).setTimeOut(10000)
                .addHeader("Accept-Encoding", "gzip,dflate,br");
    }

    public static void main(String[] args) {
        String keyword = "两会";
        Spider.create(new WechatProcesser()).addUrl("http://weixin.sogou.com/weixin?type=2&ie=utf8&query="+keyword)
                .thread(5)
                .run();
    }
}
