package com.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class TestBaiDu {


    public static void main(String[] args) {

        // TODO Auto-generated method stub
        System.setProperty("webdriver.chrome.driver", "C:/bin/chromedriver.exe");
        WebDriver dr =  new ChromeDriver();
        dr.get("http://www.baidu.com");
        dr.findElement(By.id("kw")).sendKeys("hello Selenium");
        dr.findElement(By.id("su")).click();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        dr.quit();
    }
}
