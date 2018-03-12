package com.gtja.example;

import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.monitor.SpiderMonitor;

public class MonitorExample {

    public static void main(String[] args) throws Exception {

        Spider oschinaSpider = Spider.create(new OschinaBlogPageProcesser())
                .addUrl("http://my.oschina.net/flashsword/blog");

        SpiderMonitor.instance().register(oschinaSpider);
        oschinaSpider.start();
    }
}
