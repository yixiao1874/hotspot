package com.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.concurrent.TimeUnit;

public class TestBaiDu {


    public static void main(String[] args) {

        // TODO Auto-generated method stub
        System.setProperty("webdriver.chrome.driver", "/usr/local/chromedriver");
        //System.setProperty("webdriver.chrome.driver", "C://bin/chromedriver.exe");
        //System.setProperty("webdriver.chrome.bin","/usr/bin/google-chrome");
        ChromeOptions chromeOptions = new ChromeOptions();
        //设置为 headless 模式 （必须）
        chromeOptions.addArguments("headless");
        chromeOptions.addArguments("disable-extensions");
        chromeOptions.addArguments("disable-gpu");
        chromeOptions.addArguments("no-sandbox");
        WebDriver dr =  new ChromeDriver(chromeOptions);
        dr.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        dr.get("http://www.baidu.com");
        String kw = dr.findElement(By.id("kw")).toString();
        dr.findElement(By.id("kw")).sendKeys("hello Selenium");
        dr.findElement(By.id("su")).click();
        if (kw != null){
            System.out.println(kw + "=======================");
        }else {
            System.out.println("error ========================");
        }
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        dr.quit();
    }
}
