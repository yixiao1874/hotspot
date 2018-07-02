package com.gtja.processer;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.HashMap;
import java.util.List;

/*
同花顺财经问财搜索
 */
public class IwencaiProcessor implements PageProcessor {
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);
    private final String USER_AGENT = "Chrome/64.0.3282.186";
    private static HashMap<String,Integer> hashMap = new HashMap();
    //http://www.iwencai.com/stockpick/search?tid=stockpick&qs=sl_box_main_ths&w=%E4%B8%A4%E4%BC%9A
    //<div class="em graph alignCenter graph"><a target="_blank" href="/stockpick/search?tid=stockpick&qs=stockpick_diag&ts=1&w=300172">中电环保</a></div>
    @Override
    public void process(Page page) {
        ////*[@id="tableWrap"]/div[2]/div/div[2]/div/table/tbody/tr[1]/td[4]/div/a
        List<String> stock_name = page.getHtml().xpath("//div[@class='alignCenter']//a/text()").all();
        if (stock_name.size()==0)
            System.out.println("没有获取到内容");
        for(String s:stock_name){
            System.out.println(s);
        }
    }

    @Override
    public Site getSite() {
        return site != null ? site : Site.me().setSleepTime(1000).setCycleRetryTimes(3)
                .setRetrySleepTime(1000).setUserAgent(USER_AGENT).setTimeOut(10000)
                .addHeader("Accept-Encoding", "gzip,dflate,br");
    }

    public static void main(String[] args) {
        Spider.create(new IwencaiProcessor())
                .addUrl("http://www.iwencai.com/stockpick/search?tid=stockpick&qs=sl_box_main_ths&w=%E4%B8%A4%E4%BC%9A")
                //.thread(5)
                .run();
    }
}
