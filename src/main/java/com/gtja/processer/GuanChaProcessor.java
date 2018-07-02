package com.gtja.processer;

import com.gtja.dao.SpotDao;
import com.gtja.util.StockNameUtil;
import com.gtja.util.StockUtil;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GuanChaProcessor implements PageProcessor {
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

        String currentUrl = page.getUrl().toString();
        if (page.getUrl().regex("http://www.guancha.cn/Search\\?k=.*").match()){
            String pageNumber = currentUrl.split("pi=")[1];
            Integer nextPage = Integer.parseInt(pageNumber)+1;
            page.addTargetRequest(currentUrl.replace("pi="+pageNumber,"pi="+nextPage));
        }
        List<String> detailUrls = page.getHtml().xpath("//h4[@class='module-title']//a/@href").all();
        page.addTargetRequests(detailUrls);
    }

    @Override
    public Site getSite() {
        return site != null ? site : Site.me().setSleepTime(1000).setCycleRetryTimes(3)
                .setRetrySleepTime(1000).setUserAgent(USER_AGENT).setTimeOut(10000)
                .addHeader("Accept-Encoding", "gzip,dflate,br");
    }

    public static void main(String[] args) {
        Spider spider = Spider.create(new GuanChaProcessor());
        /*List<String> urls = SpotDao.guanchaUrl();
        for (String s: urls){
            spider.addUrl(s);
        }*/
        spider.addUrl("http://www.guancha.cn/Search/?k=中美贸易战&y=1&ps=20&pi=3").thread(5).run();
        Iterator iter = hashMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            System.out.println(key + ":" + value);
        }
    }
}
