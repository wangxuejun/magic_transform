package com.dd.nio.common.operate;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URLDecoder;
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
        chromeDriver.close();
        cookie = cookie+"session_application_code=zcy.agreement";
        return cookie;
    }

    public String getCookie(){
        return this.cookie;
    }

    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver","/Users/tao.wang15/Desktop/magic_transform/src/main/resources/chromedriver");
        ChromeDriver chromeDriver = new ChromeDriver();
        chromeDriver.get("https://login.anhui.zcygov.cn/user-login/#/");
        chromeDriver.findElement(By.id("username")).sendKeys("WWW13339013301");
        chromeDriver.findElement(By.id("password")).sendKeys("WWW13339013301");
        chromeDriver.findElementByCssSelector(".ant-btn.login-btn.password-login.ant-btn-primary").click();
        //ant-btn login-btn password-login ant-btn-primary
        Set<Cookie> cookies = chromeDriver.manage().getCookies();
        cookies.stream().forEach(x->{
            System.out.println(x);
        });
        chromeDriver.get("https://www.anhui.zcygov.cn/item-center-front/publishgoods/publish?categoryId=7708&protocolId=1028&bidId=80&instanceCode=AHWC");
        Cookie zcy_log_client_uuid = chromeDriver.manage().getCookieNamed("_zcy_log_client_uuid");
        Cookie platform_code = chromeDriver.manage().getCookieNamed("platform_code");
        Cookie session = chromeDriver.manage().getCookieNamed("SESSION");
        Cookie wsid = chromeDriver.manage().getCookieNamed("wsid");
        Cookie uid = chromeDriver.manage().getCookieNamed("uid");
        Cookie user_type = chromeDriver.manage().getCookieNamed("user_type");
        Cookie tenant_code = chromeDriver.manage().getCookieNamed("tenant_code");
        Cookie institution_id = chromeDriver.manage().getCookieNamed("institution_id");
        //chromeDriver.manage().getCookieNamed("institution_id");
        System.out.println("========================================================");
        Set<Cookie> cookies1 = chromeDriver.manage().getCookies();
        cookies1.stream().forEach(x->{
            System.out.println(x);
        });
        /**
         * session_application_code=zcy.agreement
         */
        //session_application_code=zcy.agreement
        String url = "https://middle.anhui.zcygov.cn/user/apps/getAppsBasicInfo?timestamp=1652949298&path=%2Fdashboard%2Fpanel";
        String decode = URLDecoder.decode(url);
        RestTemplate restTemplate = new RestTemplate();
        //设置请求头参数
        HttpHeaders requestHeaders = new HttpHeaders();
        String cookie = "";
        for (Cookie cookie1:cookies) {
            cookie = cookie+cookie1.getName()+"="+cookie1.getValue()+"; ";
        }
        chromeDriver.close();
        cookie = cookie+"session_application_code=zcy.agreement";
        requestHeaders.add("Cookie", cookie);
        HttpEntity request = new HttpEntity(requestHeaders);
        ResponseEntity<String> anotherExchange = restTemplate.exchange(decode , HttpMethod.GET,request, String.class);
        System.out.println(anotherExchange.getBody());

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