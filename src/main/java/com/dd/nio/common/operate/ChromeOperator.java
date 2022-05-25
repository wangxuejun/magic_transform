package com.dd.nio.common.operate;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
public class ChromeOperator {

    private String chromeDriverPath;

    private ChromeDriver chromeDriver;

    private String cookie;


    public ChromeOperator(@Value("${chrome.driver.path}") String chromeDriverPath) {
        this.chromeDriverPath = chromeDriverPath;
        String init = init();
        cookie = init;
    }

    public ChromeDriver getChromeDriver(){
        return chromeDriver;
    }

    public String init(){
        cookie = "";
        System.setProperty("webdriver.chrome.driver",chromeDriverPath);
        chromeDriver = new ChromeDriver();
        chromeDriver.get("https://login.anhui.zcygov.cn/user-login/#/");
        chromeDriver.findElement(By.id("username")).sendKeys("WWW13339013301");
        chromeDriver.findElement(By.id("password")).sendKeys("WWW13339013301");
        chromeDriver.findElementByCssSelector(".ant-btn.login-btn.password-login.ant-btn-primary").click();
        Set<Cookie> cookies = chromeDriver.manage().getCookies();
        for (Cookie cookie1:cookies) {
            cookie = cookie + cookie1.getName()+"="+cookie1.getValue()+"; ";
        }
        //chromeDriver.close();
        cookie = cookie+"session_application_code=zcy.agreement";
        return cookie;
    }

    public String getCookie(){
        return this.cookie;
    }

    public void postItem(){
        chromeDriver.get("https://login.anhui.zcygov.cn/user-login/#/");
        chromeDriver.findElement(By.id("username")).sendKeys("WWW13339013301");
        chromeDriver.findElement(By.id("password")).sendKeys("WWW13339013301");
        chromeDriver.findElementByCssSelector(".ant-btn.login-btn.password-login.ant-btn-primary").click();
        ////span[text()='新闻']
        chromeDriver.findElement(By.xpath("//div[@class='module-box' and @title='商品协议']")).click();
    }

    public static void main(String[] args) throws InterruptedException {
        System.setProperty("webdriver.chrome.driver","/Users/tao.wang15/Desktop/magic_transform/src/main/resources/chromedriver");
        ChromeOptions options = new ChromeOptions();
//        options.setHeadless(true);
        ChromeDriver chromeDriver = new ChromeDriver(options);
        chromeDriver.get("https://login.anhui.zcygov.cn/user-login/#/");
        Thread.sleep(2000);
        chromeDriver.findElement(By.id("username")).sendKeys("WWW13339013301");
        chromeDriver.findElement(By.id("password")).sendKeys("WWW13339013301");
        chromeDriver.findElementByCssSelector(".ant-btn.login-btn.password-login.ant-btn-primary").click();
        chromeDriver.findElement(By.xpath("//div[@class='module-box' and @title='商品协议']")).click();
        Thread.sleep(2000);
        chromeDriver.findElement(By.xpath("//li[@data-utm-click='324414']")).click();
        Thread.sleep(3000);
        chromeDriver.findElement(By.xpath("//span[@class='category-item']/span[text()='家用电器']")).click();
        Thread.sleep(1000);
        chromeDriver.findElement(By.xpath("//span[@class='category-item']/span[text()='大家电/配件']")).click();
        Thread.sleep(1000);
        chromeDriver.findElement(By.xpath("//span[@class='category-item']/span[text()='洗衣机']")).click();
        Thread.sleep(2000);
        //chromeDriver.findElement(By.xpath("//span[@class='category-item']/span[text()='洗衣机']")).click();
        By xpath = By.xpath("//div[@data-utm-c='c846647']");
        List<String> gateGoryList = Lists.newArrayList();
        List<String> typeList = Lists.newArrayList();
        if (isJudgingElement(chromeDriver, xpath)) {
            WebElement element1 = chromeDriver.findElement(By.xpath("//div[@class='ant-list spu-list ant-list-vertical ant-list-split']"));
            List<WebElement> elements1 = element1.findElements(By.xpath("//div[@class='ant-list spu-list ant-list-vertical ant-list-split']/div/div/div/div/div/div/div/span[@class='category-item']"));
            for (WebElement element:elements1) {
                gateGoryList.add(element.getText());
            }
            //todo 判断查询出来的数据应该点击第几个span
            elements1.get(0).click();
            Thread.sleep(2000);
            By xpathType = By.xpath("//input[@placeholder='请输入型号名称']");
            if (isJudgingElement(chromeDriver, xpathType)) {
                // spu-list specification ant-list-vertical ant-list-split
                WebElement element = chromeDriver.findElement(By.xpath("//div[@class='ant-list spu-list specification ant-list-vertical ant-list-split']"));
                List<WebElement> elements = element.findElements(By.xpath("//div[@class='ant-list spu-list specification ant-list-vertical ant-list-split']/div/div/div/div/div/span/span[@class='category-item']"));
                for (WebElement webElement : elements) {
                    typeList.add(webElement.getText());
                }
                //todo 判断查询出来的数据具体型号 click
                elements.get(0).click();
            }
        }
        chromeDriver.findElement(By.xpath("//button[@class='ant-btn ant-btn-primary']")).click();
        Thread.sleep(3000);
        //1.查找商品标题 input todo填充标题
        chromeDriver.findElement(By.id("name")).clear();
        chromeDriver.findElement(By.id("name")).sendKeys("nihao");
        //2.查到通用属性 遍历
        chromeDriver.findElement(By.xpath("//div[@class='ant-select-selection\n" +
                "            ant-select-selection--single']")).click();
        Thread.sleep(3000);
        List<WebElement> elements34 = chromeDriver.findElements(By.xpath("//div[@class='brand-item']/div/span[@class='brand-item-name']"));
        for (WebElement webElement:elements34) {
            System.out.println(webElement.getText());
        }
    }


    public static String encrypt3ToMD5(String str) {
        log.debug("MD5待加密字符串：\n"+str);
        String md5 = "  ";
        try {
            md5 = DigestUtils.md5DigestAsHex(str.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return md5;
    }

    public static boolean isJudgingElement(WebDriver webDriver,By by) {
        try {
            webDriver.findElement(by);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    private static String hashHex(String input, String hashType) {
        try {
            MessageDigest md = MessageDigest.getInstance(hashType);
            md.update(input.getBytes());
            byte[] byteData = md.digest();
            StringBuilder buffer = new StringBuilder();
            byte[] var5 = byteData;
            int var6 = byteData.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                byte byteDatum = var5[var7];
                buffer.append(Integer.toString((byteDatum & 255) + 256, 16).substring(1));
            }

            return buffer.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}

/**
 * _zcy_log_client_uuid=e466a020-d74c-11ec-9108-39f440dc64a0;
 * platform_code=zcy-anhui;
 * SESSION=NzRiNmIzYWMtZjc2ZC00NWJiLWJmYzctZGE4M2Y1Y2I5ZDUy;
 * wsid=10008091317#1652948581904;
 * uid=10009559607;
 * user_type=0202;
 * tenant_code=349900;
 * institution_id=165961903659041;
 * session_application_code=zcy.agreement
 *
 *
 * _zcy_log_client_uuid=440deba0-d14e-11ec-833e-e70800b8d3ed;
 * user_type=0202;
 * tenant_code=349900;
 * institution_id=165961903659041;
 * platform_code=zcy-anhui;
 * wsid=10008091317#1652626967893;
 * uid=10009559607;
 * districtCode=349900;
 * SESSION=MWEwMTQzMmMtNDA2MS00OTE3LWE4NzgtMGRjMzQ1YTdiZmU5
 */