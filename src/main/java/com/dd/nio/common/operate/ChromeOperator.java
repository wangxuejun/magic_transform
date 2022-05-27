package com.dd.nio.common.operate;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
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
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Component
@Slf4j
public class ChromeOperator {

    private String chromeDriverPath;

    private ChromeDriver chromeDriver;

    private String cookie;


    public ChromeOperator(@Value("${chrome.driver.path}") String chromeDriverPath) {
        this.chromeDriverPath = chromeDriverPath;
        init();
    }

    public ChromeDriver getChromeDriver(){
        return chromeDriver;
    }

    public void init(){
        System.setProperty("webdriver.chrome.driver",chromeDriverPath);
        chromeDriver = new ChromeDriver();
        chromeDriver.get("https://login.anhui.zcygov.cn/user-login/#/");
        chromeDriver.findElement(By.id("username")).sendKeys("WWW13339013301");
        chromeDriver.findElement(By.id("password")).sendKeys("WWW13339013301");
        chromeDriver.findElementByCssSelector(".ant-btn.login-btn.password-login.ant-btn-primary").click();
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
//        chromeDriver.findElement(By.xpath("//div[@class='module-box' and @title='商品协议']")).click();
//        Thread.sleep(2000);
//        chromeDriver.findElement(By.xpath("//li[@data-utm-click='324414']")).click();
//        Thread.sleep(3000);
//        chromeDriver.findElement(By.xpath("//span[@class='category-item']/span[text()='家用电器']")).click();
//        Thread.sleep(1000);
//        chromeDriver.findElement(By.xpath("//span[@class='category-item']/span[text()='大家电/配件']")).click();
//        Thread.sleep(1000);
//        chromeDriver.findElement(By.xpath("//span[@class='category-item']/span[text()='洗衣机']")).click();
//        Thread.sleep(2000);
//        //chromeDriver.findElement(By.xpath("//span[@class='category-item']/span[text()='洗衣机']")).click();
//        By xpath = By.xpath("//div[@data-utm-c='c846647']");
//        List<String> gateGoryList = Lists.newArrayList();
//        List<String> typeList = Lists.newArrayList();
//        if (isJudgingElement(chromeDriver, xpath)) {
//            WebElement element1 = chromeDriver.findElement(By.xpath("//div[@class='ant-list spu-list ant-list-vertical ant-list-split']"));
//            List<WebElement> elements1 = element1.findElements(By.xpath("//div[@class='ant-list spu-list ant-list-vertical ant-list-split']/div/div/div/div/div/div/div/span[@class='category-item']"));
//            for (WebElement element:elements1) {
//                gateGoryList.add(element.getText());
//            }
//            //todo 判断查询出来的数据应该点击第几个span
//            elements1.get(0).click();
//            Thread.sleep(2000);
//            By xpathType = By.xpath("//input[@placeholder='请输入型号名称']");
//            if (isJudgingElement(chromeDriver, xpathType)) {
//                // spu-list specification ant-list-vertical ant-list-split
//                WebElement element = chromeDriver.findElement(By.xpath("//div[@class='ant-list spu-list specification ant-list-vertical ant-list-split']"));
//                List<WebElement> elements = element.findElements(By.xpath("//div[@class='ant-list spu-list specification ant-list-vertical ant-list-split']/div/div/div/div/div/span/span[@class='category-item']"));
//                for (WebElement webElement : elements) {
//                    typeList.add(webElement.getText());
//                }
//                //todo 判断查询出来的数据具体型号 click
//                elements.get(0).click();
//            }
//        }
//        chromeDriver.findElement(By.xpath("//button[@class='ant-btn ant-btn-primary']")).click();
//        Thread.sleep(3000);
        //1.查找商品标题 input todo填充标题
        chromeDriver.get("https://www.anhui.zcygov.cn/item-center-front/publishgoods/publish?categoryId=7903&spuId=100785469&protocolId=1028&bidId=80&instanceCode=AHWC");
        Thread.sleep(3000);
        WebElement elementByCssSelector = chromeDriver.findElementByCssSelector("label[title='标准规格']");
        System.out.println(elementByCssSelector.getText());
        WebElement element = elementByCssSelector.findElement(By.xpath("../following-sibling::div"));
        //form
//        WebElement element1 = element.findElement(By.xpath(".//form"));
//        WebElement element2 = element1.findElement(By.xpath(".//input[@class='ant-radio-input']"));
//        System.out.println(element2.getAttribute("class"));
//        element2.click();
//        WebElement element3 = element1.findElement(By.xpath(".//span[@class='zcy-tenant-addressCode-undefined ant-cascader-picker zcy-tenant-addressCode-undefined']"));
//        element3.click();
        WebElement element1 = element.findElement(By.xpath(".//input[@type='checkbox']"));
        System.out.println(element1.getAttribute("value"));
        WebElement element2 = element.findElement(By.xpath(".//a[text()='点击添加']"));
        element2.click();
        //Thread.sleep(5000);
//        wat(chromeDriver,By.xpath("//ul[@class='ant-select-dropdown-menu  ant-select-dropdown-menu-root ant-select-dropdown-menu-vertical']"));
//        WebElement elementDiv = chromeDriver.findElement(By.xpath("//ul[@class='ant-select-dropdown-menu  ant-select-dropdown-menu-root ant-select-dropdown-menu-vertical']"));
//        List<WebElement> element2 = elementDiv.findElements(By.xpath(".//li[@class='ant-select-dropdown-menu-item']"));
//        WebElement judgingElement = isJudgingElement(elementDiv, By.xpath(".//li[@class='ant-select-dropdown-menu-item ant-select-dropdown-menu-item-selected']"));
//        WebElement judgingElementActive = isJudgingElement(elementDiv,By.xpath(".//li[@class='ant-select-dropdown-menu-item ant-select-dropdown-menu-item-active']"));
//        WebElement judgingElementActiveSelect = isJudgingElement(elementDiv,By.xpath(".//li[@class='ant-select-dropdown-menu-item ant-select-dropdown-menu-item-active ant-select-dropdown-menu-item-selected']"));
//        if (Objects.nonNull(judgingElement)) {
//            element2.add(judgingElement);
//        }
//        if (Objects.nonNull(judgingElementActive)){
//            element2.add(judgingElementActive);
//        }
//        if (Objects.nonNull(judgingElementActiveSelect)){
//            element2.add(judgingElementActiveSelect);
//        }
//        for (WebElement webElement:element2) {
//            System.out.println(webElement.getAttribute("innerText"));
//            if (webElement.getAttribute("innerText").equals("香山")){
//                webElement.click();
//            }
//        }

//        List<WebElement> elements = elementDiv.findElements(By.xpath("//span[@class='brand-item-name']"));
//        for (WebElement webElement:elements) {
//            System.out.println(webElement.getText());
//            System.out.println(webElement.getAttribute("innerText"));
//        }
        String aClass = element.getAttribute("class");
        System.out.println(aClass);
//        chromeDriver.findElement(By.id("name")).clear();
//        chromeDriver.findElement(By.id("name")).sendKeys("nihao");
//        chromeDriver.findElement(By.xpath("/html/body/div[2]/div/div/div/div/div[1]/div/div[7]/div[2]/div[1]/div/div/div/div/div[2]/div/span/div/div[2]/div[1]/div[2]/div")).click();
//        WebElement element3 = chromeDriver.findElement(By.xpath("/html/body/div[4]/div/div[2]/div/div[1]/div[2]/div[2]/span/div/span/input"));
//        element3.sendKeys("/Users/tao.wang15/Desktop/magic_transform/src/main/resources/aa.png");
//        //2. 找到不不同属性
//        WebElement basicEle = chromeDriver.findElement(By.id("基本信息"));
//        List<WebElement> elements = basicEle.findElements(By.xpath("div/div[@class='zcy-panel-sub zcy-panel-sub-has-child']"));
//        for (WebElement webElement :elements) {
//            List<WebElement> eleTypes = webElement.findElements(By.xpath("div[@class='zcy-panel-sub-title zcy-panel-sub-bordered zcy-panel-sub-bordered-has-child']/span"));
//            for (WebElement element:eleTypes) {
//                System.out.println(element.getText());
//                if ("通用属性".equals(element.getText())){
//                    //todo 判断是什么属性
//                    List<WebElement> elements1 = webElement.findElements(By.xpath("div/div/div[@class='ant-col-12 zcy-form-grid-col-bordered']"));
//                    for (WebElement element1:elements1) {
//                        WebElement element2 = isJudgingElement(element1,By.xpath("div/div/label[@class='ant-form-item-required']"));
//                        if (Objects.nonNull(element2)) {
//                            String type = getType(element1);
//                            if (type.equals("input")){
//                                fillingInput(element1);
//                            }else if (type.equals("form")){
//                                fillingForm(element1);
//                            }else {
//                                fillingSelect(element1,"海尔");
//                            }
//                        }
//                        //对比数据库的数据判断是什么类型
//
//                    }
//                }
//            }
//        }
        //2.查到通用属性 遍历
    }

    public static void wat(ChromeDriver driver,By by) {
        Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
                .withTimeout(30, TimeUnit.SECONDS)
                .pollingEvery(8, TimeUnit.SECONDS)
                .ignoring(NoSuchElementException.class);
        WebElement clickseleniumlink = wait.until(new Function<WebDriver, WebElement>() {
            @Override
            public WebElement apply(WebDriver driver) {
                return driver.findElement(by);
            }
        });
    }

    public static String getType(WebElement element1){
        //查找是否有input框 并且找不到form表单 这种是纯input
        WebElement input = isJudgingElement(element1, By.xpath("div/div[@class='ant-form-item-control-wrapper ant-col-xs-18 ant-col-sm-18']/div/span/div/div/div/div/ul/li/div/input"));
        if (Objects.nonNull(input)){
            return "input";
        }
        //如果有没有form表单 这种是form
        WebElement judgingElement = isJudgingElement(element1, By.xpath("div/div/div/span/div/form[@class='ant-form ant-form-horizontal']"));
        if (Objects.nonNull(judgingElement)){
            return "form";
        }
        return "select";
    }

    public static void fillingSelect(WebElement element,String brand) throws InterruptedException {
        element.findElement(By.xpath("div/div/div/span[@class='ant-form-item-children']")).click();
        Thread.sleep(3000);
        //List<WebElement> elements34 = element.findElements(By.xpath("//div[@class='brand-item']/div/span[@class='brand-item-name']"));
        List<WebElement> elements34 = element.findElements(By.xpath("//ul[@class='ant-select-dropdown-menu  ant-select-dropdown-menu-root ant-select-dropdown-menu-vertical']/li"));
        for (WebElement webElement:elements34) {
            WebElement element1 = webElement.findElement(By.xpath("div/div/span"));
            if (element1.getText().contains(brand)){
                webElement.click();
                break;
            }
        }
    }

    public static void fillingInput(WebElement element){
        element.findElement(By.xpath("div/div[@class='ant-form-item-control-wrapper ant-col-xs-18 ant-col-sm-18']/div/span/div/div/div/div/ul/li/div/input"))
                .sendKeys("58S");
    }

    public static void fillingForm(WebElement element){
        WebElement form = element.findElement(By.xpath("div/div/div/span/div/form[@class='ant-form ant-form-horizontal']"));
        WebElement span = form.findElement(By.xpath("div/div/div/span/div/label/span[text()='境内']"));
        span.findElement(By.xpath("preceding-sibling::input")).click();
        form.findElement(By.xpath("span/span/input")).click();
        WebElement element1 = element.findElement(By.xpath("//div[@class='ant-cascader-menus ant-cascader-menus-placement-bottomLeftant-cascader-menus ant-cascader-menus-placement-bottomLeft']"));
        List<WebElement> elements = element1.findElements(By.xpath("div/ul/li"));
        //todo
    }

    public static WebElement  isJudgingElement(WebElement webDriver,By by) {
        try {
            WebElement element = webDriver.findElement(by);
            return element;
        }catch (Exception e){
            return null;
        }
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