package com.gtja.processer;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.JsonPathSelector;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class WechatPublicProcessor implements PageProcessor {
    private static final String CHARSET = "utf-8";
    private static final String FORMAT = "JPG";
    private final String PATH = "E:\\pic_qrcode_default.jpg";
    private final String USER_AGENT = "Chrome/64.0.3282.186";
    private final String PICTURE_PATH =
            "https://res.wx.qq.com/mpres/zh_CN/htmledition/modules/qrcheck/pic_qrcode_default.jpg";
    String loginUrl;

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

    /**
     * 解析二维码
     *
     * @param file
     *            二维码图片
     * @return
     * @throws Exception
     */
    public static String decode(File file) throws Exception {
        BufferedImage image;
        image = ImageIO.read(file);
        if (image == null) {
            return null;
        }
        BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        Result result;
        Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>();
        hints.put(DecodeHintType.CHARACTER_SET, CHARSET);
        result = new MultiFormatReader().decode(bitmap, hints);
        String resultStr = result.getText();
        return resultStr;
    }

    public void analysis(String url,String path) throws Exception{
        URLConnection urlConn = new URL(url).openConnection();

        System.out.println("Date: " + new Date(urlConn.getDate()));
        System.out.println("Content-Type: " + urlConn.getContentType());

        int length = urlConn.getContentLength();
        System.out.println("Content-Lentgth: " + length);
        OutputStream out = new FileOutputStream(path);

        if (length > 0) {
            System.out.println("========== Content ==========");
            InputStream input = urlConn.getInputStream();
            int i = length;
            int len;
            byte[] car = new byte[1024];
            while((len = input.read(car))!=-1){
                out.write(car, 0, len);
                out.flush();
            }
            input.close();
            out.close();
        } else {
            System.out.println("No Content.");
        }


    }

    public void login()
    {
        System.setProperty("webdriver.chrome.driver", "C:/bin/chromedriver.exe");
        WebDriver dr =  new ChromeDriver();
        dr.get("https://mp.weixin.qq.com/");
        // 等待加载完成
        dr.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        // 获取页面元素
        WebElement elemUsername = dr.findElement(By.name("account"));
        WebElement elemPassword = dr.findElement(By.name("password"));
        WebElement btn = dr.findElement(By.className("btn_login"));
        //WebElement rememberMe = dr.findElement(By.className("frm_checkbox"));
        // 操作页面元素
        elemUsername.clear();
        elemPassword.clear();
        /*elemUsername.sendKeys("1856168515@qq.com");
        elemPassword.sendKeys("jkdpofkjaposf");*/
        elemUsername.sendKeys("1499957726@qq.com");
        elemPassword.sendKeys("Lcptbtp6573569");
        //rememberMe.click();
        btn.click();
        dr.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        try {
            Thread.sleep(5000);
            //<img src="/cgi-bin/loginqrcode?action=getqrcode&amp;param=4300&amp;rd=582" class="weui-desktop-qrcheck__img js_qrcode">
            //dr.get(dr.getCurrentUrl());
            //String currentUrl = dr.findElement(By.xpath("//*[@id=\"app\"]/div[2]/div/div[2]/div[1]/div/img")).getAttribute("src");
            //https://mp.weixin.qq.com/cgi-bin/loginqrcode?action=getqrcode&param=4300&rd=576
            //dr.get("https://res.wx.qq.com/mpres/zh_CN/htmledition/modules/qrcheck/pic_qrcode_default.jpg");
            analysis(PICTURE_PATH,PATH);
            loginUrl = decode(new File(PATH));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //模拟手机扫码登录
        /*https://mp.weixin.qq.com/wap/loginauthqrcode?action=scan&
        qrticket=77d34ddf04ee268e3968d0bd997dcb43&lang=zh_CN&devicetype=android-23&version=26060135
&pass_ticket=8hkj7OqKS8R%2Ftgg9jQ5ktZnN4W1Ak1ivuoHyBppvrqHFw67VHRdu5N%2BRKu9lU%2Bid&wx_header=1*/
        String qrticket = loginUrl.substring(loginUrl.lastIndexOf("/")+1);
        System.out.println(loginUrl);
        System.out.println(qrticket);
        String targetUrl = "https://mp.weixin.qq.com/wap/loginauthqrcode?action=scan&\n" +
                "qrticket="+qrticket+"&lang=zh_CN&devicetype=android-23&version=26060135\n" +
                "&pass_ticket=8hkj7OqKS8R%2Ftgg9jQ5ktZnN4W1Ak1ivuoHyBppvrqHFw67VHRdu5N%2BRKu9lU%2Bid&wx_header=1";
        ChromeOptions options = new ChromeOptions();
        //设置user agent   micromessage    MicroMessenger

        //options.addArguments("User-Agent","Mozilla/5.0 (Linux; Android 6.0.1; SM-G9250 Build/MMB29K; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/57.0.2987.132 MQQBrowser/6.2 TBS/043909 Mobile Safari/537.36 MicroMessenger/6.6.1.1220(0x26060135) NetType/WIFI Language/zh_CN");
        options.addArguments("--user-agent=Mozilla/5.0 (Linux; Android 6.0.1; SM-G9250 Build/MMB29K; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/57.0.2987.132 MQQBrowser/6.2 TBS/043909 Mobile Safari/537.36 micromessage/6.6.1.1220(0x26060135) NetType/WIFI Language/zh_CN");
        options.addArguments("Host=mp.weixin.qq.com");
        options.addArguments("Connection=keep-alive");
        options.addArguments("x-wechat-key=4170cd7d6b842079d2a2848958f0b5b1a3e7c8f4887249003286c7acd5162a03daeae117b243ad518ff4c5fdaa4727d96365723a0e74638ec2953726c4047c11897df407356d19aacc95bca056f6be97");
        options.addArguments("x-wechat-uin=Nzg1NzA5MjEw");
        options.addArguments("Accept=text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,image/wxpic,image/sharpp,image/apng,*/*;q=0.8");
        options.addArguments("Accept-Encoding=gzip, deflate");
        options.addArguments("Q-UA2=QV=3&PL=ADR&PR=WX&PP=com.tencent.mm&PPVN=6.6.1&TBSVC=43602&CO=BK&COVC=043909&PB=GE&VE=GA&DE=PHONE&CHID=0&LCID=9422&MO= SM-G9250 &RL=1440*2560&OS=6.0.1&API=23");
        options.addArguments("Accept-Language=zh-CN,en-US;q=0.8");
        options.addArguments("Q-GUID=07cb8f58e413041bb26a32c913b788cb");
        options.addArguments("Q-Auth=31045b957cf33acf31e40be2f3e71c5217597676a9729f1b");
        //实例化chrome对象，并加入选项

        WebDriver driver = new ChromeDriver(options);
        driver.get(targetUrl);
        driver.findElement(By.id("js_allow")).click();
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

    public static void main(String[] args) throws Exception{
        WechatPublicProcessor wechatPublicProcesser = new WechatPublicProcessor();
        /*wechatPublicProcesser.analysis(
                "https://res.wx.qq.com/mpres/zh_CN/htmledition/modules/qrcheck/pic_qrcode_default.jpg",
                "E:\\pic_qrcode_default.jpg");*/
        wechatPublicProcesser.login();
        /*Spider.create(wechatPublicProcesser)//.setDownloader(downloader)
                .addUrl("https://mp.weixin.qq.com/cgi-bin/appmsg?" +
                        "token="+token+"&lang=zh_CN&f=json&ajax=1&random=0.9915912628321586" +
                        "&action=list_ex&begin=0&count=5&query=&fakeid=MjM5MDQ4MzU5NQ%3D%3D&type=9")
                .thread(5)
                .run();*/
    }
}
