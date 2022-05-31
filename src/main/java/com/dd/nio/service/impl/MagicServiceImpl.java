package com.dd.nio.service.impl;

import com.dd.nio.common.exception.ServiceException;
import com.dd.nio.common.operate.ChromeOperator;
import com.dd.nio.common.response.ResultData;
import com.dd.nio.common.utils.PicUtils;
import com.dd.nio.entity.*;
import com.dd.nio.entity.vo.AttributeTypeVo;
import com.dd.nio.entity.vo.GoodVo;
import com.dd.nio.mapper.UserMapper;
import com.dd.nio.service.*;
import com.google.common.collect.Sets;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
public class MagicServiceImpl implements MagicService {

    @Autowired
    private IGoodService goodService;

    @Autowired
    @SuppressWarnings("all")
    private IAttributeTypeService iAttributeTypeService;

    @Autowired
    @SuppressWarnings("all")
    private IGoodPriceService iGoodPriceService;

    @Autowired
    @SuppressWarnings("all")
    private IGoodImageService iGoodImageService;

    @Autowired
    private UserMapper userMapper;

    @Value("${chrome.driver.path}")
    private String chromeDriverPath;


    @Override
    public ResultData fillingGoods(Long userId) {
        User user = userMapper.selectById(userId);
        if (Objects.isNull(user)){
            throw new ServiceException("查无此用户");
        }
        System.setProperty("webdriver.chrome.driver",chromeDriverPath);
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless");
        chromeOptions.addArguments("--disable-gpu");//谷歌文档提到需要加上这个属性来规避bug
        chromeOptions.addArguments("--no-sandbox");// “–no - sandbox”参数是让Chrome在root权限下跑
        chromeOptions.addArguments("--disable-dev-shm-usage");
        chromeOptions.addArguments("lang=zh_CN.UTF-8");
        chromeOptions.addArguments("window-size=1920x1080");
        ChromeDriver chromeDriver = new ChromeDriver(chromeOptions);
        chromeDriver.get("https://login.anhui.zcygov.cn/user-login/#/");
        wat(chromeDriver,By.id("username"));
        chromeDriver.findElement(By.id("username")).sendKeys(user.getShopUsername());
        chromeDriver.findElement(By.id("password")).sendKeys(user.getShopPassword());
        chromeDriver.findElementByCssSelector(".ant-btn.login-btn.password-login.ant-btn-primary").click();
        List<GoodVo> pageGoods = goodService.getPageGoods();
        for (GoodVo goodVo:pageGoods) {
            chromeDriver.get("https://www.anhui.zcygov.cn/item-center-front/publishgoods/publish?categoryId=5984&protocolId=1028&bidId=80&instanceCode=AHWC");
            try {
                analyGood(chromeDriver,goodVo);
                goodService.complete(goodVo.getGood());
            }catch (Exception e){
                log.error(e.getMessage());
                log.error(goodVo.getGood().getName());
                log.error(goodVo.getGood().getId().toString());
                goodService.deleteGood(goodVo.getGood());
            }
        }
        chromeDriver.close();
        return ResultData.success();
    }

    @Override
    public ResultData getWrite() {
        return ResultData.success(goodService.getWriteCount());
    }


    public void analyGood(ChromeDriver chromeDriver,GoodVo goodVo) {
        Good good = goodVo.getGood();
        List<GoodAttribute> attributes = goodVo.getAttributes();
        Set<String> objects = Sets.newHashSet();
        List<GoodAttribute> anAtts = sortList(attributes, "是否需要安装", 0);
        List<GoodAttribute> pinAtts = sortList(anAtts, "品牌", 0);
        AtomicReference<GoodAttribute> yun = null;
        pinAtts.forEach(x->{
            objects.add(x.getAttributeKey());
            if (x.getAttributeKey().equals("运费模版")){
                yun.set(x);
            }
        });
        if (!objects.contains("产地")){
            GoodAttribute attribute = new GoodAttribute();
            attribute.setAttributeKey("产地");
            attribute.setAttributeValue("安徽省,合肥市,长丰县");
            pinAtts.add(attribute);
        }
        if (!objects.contains("运费模版")){
            GoodAttribute attribute = new GoodAttribute();
            attribute.setAttributeKey("运费模版");
            attribute.setAttributeValue("华东");
            pinAtts.add(attribute);
        }else {
            pinAtts.remove(yun.get());
            pinAtts.add(yun.get());
        }
        Set<String> imageSet = new HashSet<>();
        Integer ciShu = 0;
        wat(chromeDriver, By.id("name"));
        WebElement name = chromeDriver.findElement(By.id("name"));
        name.sendKeys(good.getName());
        for (GoodAttribute attribute:pinAtts) {
            //写入商品标题
            String attributeKey = attribute.getAttributeKey();
            if (attributeKey.equals("品牌")){
                wat(chromeDriver, By.xpath("//label[@title='品牌']"));
                WebElement elementByCssSelector = chromeDriver.findElementByXPath("//label[@title='品牌']");
                WebElement element = elementByCssSelector.findElement(By.xpath("../following-sibling::div"));
                fillingPinpai(element,attribute.getAttributeValue(),chromeDriver);
            }else if (attributeKey.equals("型号")){
                WebElement elementByCssSelector = chromeDriver.findElementByCssSelector("label[title='型号']");
                WebElement element = elementByCssSelector.findElement(By.xpath("../following-sibling::div"));
                fillingInput(element,attribute.getAttributeValue());
            }else if (attributeKey.equals("产地")){
                WebElement elementByCssSelector = chromeDriver.findElementByCssSelector("label[title='产地']");
                WebElement element = elementByCssSelector.findElement(By.xpath("../following-sibling::div"));
                fillingForm(element,attribute.getAttributeValue(),chromeDriver);
            }else if (attributeKey.equals("电商平台链接")){
                WebElement elementByCssSelector = chromeDriver.findElementByCssSelector("label[title='电商平台链接']");
                WebElement element = elementByCssSelector.findElement(By.xpath("../following-sibling::div"));
                fillingInput(element,attribute.getAttributeValue());
            }else if (attributeKey.equals("计量单位")){
                WebElement elementByCssSelector = chromeDriver.findElementByCssSelector("label[title='计量单位']");
                WebElement element = elementByCssSelector.findElement(By.xpath("../following-sibling::div"));
                fillingInput(element,attribute.getAttributeValue());
            }else if (attributeKey.equals("生产厂商")){
                WebElement elementByCssSelector = chromeDriver.findElementByCssSelector("label[title='生产厂商']");
                WebElement element = elementByCssSelector.findElement(By.xpath("../following-sibling::div"));
                fillingInput(element,attribute.getAttributeValue());
            }else if (attributeKey.equals("是否需要安装")){
                WebElement elementByCssSelector = chromeDriver.findElementByCssSelector("label[title='是否需要安装']");
                WebElement element = elementByCssSelector.findElement(By.xpath("../following-sibling::div"));
                fillingAnZhuang(element,attribute.getAttributeValue(),chromeDriver);
            }else if (attributeKey.equals("规格")){
                AttributeTypeVo attributeVo = iAttributeTypeService.getAttributeVo(Long.valueOf(attribute.getAttributeValue()));
                String s = attributeVo.getAttributeType().getType();
                fillingGoodAttribute(chromeDriver,s.substring(0, s.length() - 1),attributeVo,imageSet);
            }else if (attributeKey.equals("价格")){
                GoodPrice byId = iGoodPriceService.getById(Long.valueOf(attribute.getAttributeValue()));
                filingPrice(chromeDriver,byId);
            }else if (attributeKey.equals("图片")){
                GoodImage byId = iGoodImageService.getById(Long.valueOf(attribute.getAttributeValue()));
                fillingImage(chromeDriver,byId, imageSet, ciShu);
                ciShu ++ ;
            }else if (attributeKey.equals("商品详情")){
                fillingGoodDetail(chromeDriver,attribute.getAttributeValue());
            }else if (attributeKey.equals("运费模版")){
                WebElement id = chromeDriver.findElement(By.id("运费信息"));
                //WebElement elementByCssSelector = chromeDriver.findElementByCssSelector("label[title='运费模版']");
                WebElement element = id.findElement(By.xpath(".//div[@class='ant-form-item-control-wrapper']"));
                fillingYunfei(element,chromeDriver);
            }
        }
        WebElement element = chromeDriver.findElement(By.xpath("//div[@class='zcy-breadcrumb-content zcy-breadcrumb-fixed clearfix']"));
        WebElement button = element.findElement(By.xpath(".//button[@class='ant-btn ant-btn-primary']"));
        chromeDriver.executeScript("arguments[0].scrollIntoView(false);", button);
        System.out.println("button======"+button.getAttribute("class"));
        button.click();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void fillingYunfei(WebElement element,ChromeDriver chromeDriver) {
        element.findElement(By.xpath(".//div[@class='ant-select-selection\n" +
                "            ant-select-selection--single']")).click();
        chromeDriver.executeScript("arguments[0].scrollIntoView();", element);
        List<WebElement> elements = chromeDriver.findElements(By.xpath("//ul[@class='ant-select-dropdown-menu  ant-select-dropdown-menu-root ant-select-dropdown-menu-vertical']"));
        WebElement elementDiv = elements.get(elements.size()-1);
        wat(chromeDriver, elementDiv,By.xpath(".//li[@class='ant-select-dropdown-menu-item']"));
        List<WebElement> element2 = elementDiv.findElements(By.xpath(".//li[@class='ant-select-dropdown-menu-item']"));
        WebElement judgingElement = isJudgingElement(elementDiv, By.xpath(".//li[@class='ant-select-dropdown-menu-item ant-select-dropdown-menu-item-selected']"));
        WebElement judgingElementActive = isJudgingElement(elementDiv,By.xpath(".//li[@class='ant-select-dropdown-menu-item ant-select-dropdown-menu-item-active']"));
        WebElement judgingElementActiveSelect = isJudgingElement(elementDiv,By.xpath(".//li[@class='ant-select-dropdown-menu-item ant-select-dropdown-menu-item-active ant-select-dropdown-menu-item-selected']"));
        if (Objects.nonNull(judgingElement)) {
            element2.add(judgingElement);
        }
        if (Objects.nonNull(judgingElementActive)){
            element2.add(judgingElementActive);
        }
        if (Objects.nonNull(judgingElementActiveSelect)){
            element2.add(judgingElementActiveSelect);
        }
        for (WebElement webElement:element2) {
            System.out.println(webElement.getAttribute("innerText"));
            if (webElement.getAttribute("innerText").equals("华东")){
                webElement.click();
            }
        }
    }


    private void fillingGoodDetail(ChromeDriver chromeDriver, String attributeValue) {
        String s = deleteN(attributeValue);
        String js = "document.querySelector('#ueditor_0').contentDocument.querySelector('body').innerHTML='"+s+"'";
        chromeDriver.executeScript(js);
        WebElement element = chromeDriver.findElement(By.xpath("//iframe"));
        chromeDriver.switchTo().frame("ueditor_0");
        chromeDriver.findElement(By.xpath("./html/body")).sendKeys(Keys.ENTER);
        chromeDriver.switchTo().defaultContent();
    }

    private void fillingImage(ChromeDriver chromeDriver, GoodImage image, Set<String> imageSet,Integer count) {
        WebElement element = chromeDriver.findElement(By.xpath("//div[@class='uploadImage']"));
        List<WebElement> elements = element.findElements(By.xpath(".//div[@class='image-upload']"));
        WebElement webElement = elements.get(count);
        waitClick(chromeDriver,webElement.findElement(By.xpath(".//div[@class='image-box']")));
        webElement.findElement(By.xpath(".//div[@class='image-box']")).click();
        upload(chromeDriver, image.getImageUrl(), imageSet,"产品图片");
        try {
            Thread.sleep(500);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void filingPrice(ChromeDriver chromeDriver, GoodPrice price) {
        WebElement element = chromeDriver.findElement(By.xpath("//div[@class='ant-table ant-table-middle ant-table-bordered ant-table-scroll-position-left']"));
        WebElement elementPrice = element.findElement(By.xpath(".//td[@class='fullPriceYuan ant-table-row-cell-break-word']"));
        List<WebElement> elements = elementPrice.findElements(By.xpath(".//input"));
        for (int i=0;i<elements.size();i++) {
            if (i==0) {
                elements.get(i).sendKeys(price.getMarketPrice().toString());
            }
            if (i==1){
                elements.get(i).sendKeys(price.getAgreementPrice().toString());
            }
        }
        WebElement elementStock = element.findElement(By.xpath(".//td[@class='stockQuantity ant-table-row-cell-break-word']"));
        elementStock.findElement(By.xpath(".//input")).sendKeys(price.getStock().toString());
    }

    public void fillingAnZhuang(WebElement element,String brand,ChromeDriver chromeDriver){
        element.findElement(By.xpath(".//div[@class='ant-select-selection\n" +
                "            ant-select-selection--single']")).click();
        WebElement elementDiv = chromeDriver.findElements(By.xpath("//ul[@class='ant-select-dropdown-menu  ant-select-dropdown-menu-root ant-select-dropdown-menu-vertical']")).get(1);
        wat(chromeDriver, By.xpath("//li[@class='ant-select-dropdown-menu-item ant-select-dropdown-menu-item-disabled']"));
        List<WebElement> element2 = elementDiv.findElements(By.xpath(".//li[@class='ant-select-dropdown-menu-item']"));
        WebElement judgingElement = isJudgingElement(elementDiv, By.xpath(".//li[@class='ant-select-dropdown-menu-item ant-select-dropdown-menu-item-selected']"));
        WebElement judgingElementActive = isJudgingElement(elementDiv,By.xpath(".//li[@class='ant-select-dropdown-menu-item ant-select-dropdown-menu-item-active']"));
        WebElement judgingElementActiveSelect = isJudgingElement(elementDiv,By.xpath(".//li[@class='ant-select-dropdown-menu-item ant-select-dropdown-menu-item-active ant-select-dropdown-menu-item-selected']"));
        if (Objects.nonNull(judgingElement)) {
            element2.add(judgingElement);
        }
        if (Objects.nonNull(judgingElementActive)){
            element2.add(judgingElementActive);
        }
        if (Objects.nonNull(judgingElementActiveSelect)){
            element2.add(judgingElementActiveSelect);
        }
        for (WebElement webElement:element2) {
            System.out.println(webElement.getAttribute("innerText"));
            if (webElement.getAttribute("innerText").equals(brand)){
                webElement.click();
            }
        }
    }

    public void fillingPinpai(WebElement element,String brand,ChromeDriver chromeDriver){
        element.findElement(By.xpath(".//div[@class='ant-select-selection\n" +
                "            ant-select-selection--single']")).click();
        waitV(chromeDriver,By.xpath("//ul[@class='ant-select-dropdown-menu  ant-select-dropdown-menu-root ant-select-dropdown-menu-vertical']"));
        WebElement elementDiv = chromeDriver.findElements(By.xpath("//ul[@class='ant-select-dropdown-menu  ant-select-dropdown-menu-root ant-select-dropdown-menu-vertical']")).get(0);
        wat(chromeDriver, By.xpath("//li[@class='ant-select-dropdown-menu-item']/div/div/span[@class='brand-item-name']"));
        List<WebElement> element2 = elementDiv.findElements(By.xpath(".//li[@class='ant-select-dropdown-menu-item']"));
        WebElement judgingElement = isJudgingElement(elementDiv, By.xpath(".//li[@class='ant-select-dropdown-menu-item ant-select-dropdown-menu-item-selected']"));
        WebElement judgingElementActive = isJudgingElement(elementDiv,By.xpath(".//li[@class='ant-select-dropdown-menu-item ant-select-dropdown-menu-item-active']"));
        WebElement judgingElementActiveSelect = isJudgingElement(elementDiv,By.xpath(".//li[@class='ant-select-dropdown-menu-item ant-select-dropdown-menu-item-active ant-select-dropdown-menu-item-selected']"));
        if (Objects.nonNull(judgingElement)) {
            element2.add(judgingElement);
        }
        if (Objects.nonNull(judgingElementActive)){
            element2.add(judgingElementActive);
        }
        if (Objects.nonNull(judgingElementActiveSelect)){
            element2.add(judgingElementActiveSelect);
        }
        Boolean isCLick = false;
        for (WebElement webElement:element2) {
            System.out.println(webElement.getAttribute("innerText"));
            if (webElement.getAttribute("innerText").equals(brand)){
                webElement.click();
                isCLick = true;
            }
        }
        if (!isCLick){
            for (WebElement webElement:element2) {
                System.out.println(webElement.getAttribute("innerText"));
                if (webElement.getAttribute("innerText").contains("其他")){
                    webElement.click();
                }
            }
        }
    }

    public static void fillingInput(WebElement element,String input){
        WebElement element1 = element.findElement(By.xpath(".//input"));
        //判断input是否可以输入
        String disabled = element1.getAttribute("disabled");
        if (StringUtils.isEmpty(disabled)){
            System.out.println(element1.getAttribute("class"));
            element1.sendKeys(input);
        }
    }

    public static void fillingForm(WebElement element,String productPlace,ChromeDriver chromeDriver){
        List<String> list = Arrays.asList(productPlace.split(","));
        WebElement element1 = element.findElement(By.xpath(".//form"));
        WebElement element2 = element1.findElement(By.xpath(".//input[@class='ant-radio-input']"));
        chromeDriver.executeScript("arguments[0].scrollIntoView(false);", element2);
        element2.click();
        WebElement element3 = element1.findElement(By.xpath(".//span[@class='zcy-tenant-addressCode-undefined ant-cascader-picker zcy-tenant-addressCode-undefined']"));
        element3.click();
        //todo
        wat(chromeDriver, By.xpath("//ul[@class='ant-cascader-menu']"));
        WebElement element4 = chromeDriver.findElements(By.xpath("//ul[@class='ant-cascader-menu']")).get(0);
        wat(chromeDriver,element4,By.xpath(".//li"));
        List<WebElement> elements = element4.findElements(By.xpath(".//li"));
        for (WebElement webElement:elements){
            if (webElement.getAttribute("innerText").equals(list.get(0))){
                webElement.click();
            }
        }
        wat(chromeDriver, By.xpath("//ul[@class='ant-cascader-menu'][2]"));
        WebElement element5 = chromeDriver.findElements(By.xpath("//ul[@class='ant-cascader-menu']")).get(1);
        wat(chromeDriver,element4,By.xpath(".//li"));
        List<WebElement> elements1 = element5.findElements(By.xpath(".//li"));
        for (WebElement webElement:elements1){
            if (webElement.getAttribute("innerText").equals(list.get(1))){
                webElement.click();
            }
        }
        wat(chromeDriver, By.xpath("//ul[@class='ant-cascader-menu'][3]"));
        WebElement element6 = chromeDriver.findElements(By.xpath("//ul[@class='ant-cascader-menu']")).get(2);
        wat(chromeDriver,element4,By.xpath(".//li"));
        List<WebElement> elements2 = element6.findElements(By.xpath(".//li"));
        for (WebElement webElement:elements2){
            if (webElement.getAttribute("innerText").equals(list.get(2))){
                webElement.click();
            }
        }
    }

    public void fillingGoodAttribute(ChromeDriver chromeDriver,String type,AttributeTypeVo attributeVo,Set<String> imageSet){
        //查询勾选的checkbox 的index
        WebElement elementGui = chromeDriver.findElement(By.xpath("//span[text()='销售规格']"));
        WebElement elementImage = elementGui.findElement(By.xpath("../following-sibling::form"));
        System.out.println(elementImage.getAttribute("class"));
        wat(chromeDriver,By.xpath("//a[@class='attribute-add-button']"));
        WebElement elementA = chromeDriver.findElement(By.xpath("//a[@class='attribute-add-button']"));
        System.out.println(elementA.getAttribute("innerText"));
        chromeDriver.executeScript("arguments[0].scrollIntoView(false);", elementA);
        elementA.click();
        wat(chromeDriver,By.xpath("//div[@class='ant-popover ant-popover-placement-bottom']"));
        WebElement elementBottom = chromeDriver.findElement(By.xpath("//div[@class='ant-popover ant-popover-placement-bottom']"));
        WebElement element2 = elementBottom.findElement(By.xpath(".//input"));
        element2.sendKeys(type);
        elementBottom.findElement(By.xpath(".//button[@class='ant-btn ant-btn-primary ant-btn-sm']")).click();
        List<WebElement> elementsNew = elementImage.findElements(By.xpath(".//input[@type='checkbox']"));
        elementsNew.get(elementsNew.size()-1).click();
        if (Objects.nonNull(attributeVo.getAttributeImageBlob())){
            WebElement imageEle = chromeDriver.findElement(By.xpath("//label[@title='规格图片']"));
            WebElement webElement = imageEle.findElement(By.xpath("../following-sibling::div"));
            List<WebElement> elementsImgs = webElement.findElements(By.xpath(".//div[@class='image-upload']"));
            for (WebElement img:elementsImgs){
                String innerText = img.findElement(By.xpath(".//div[@class='image-title']")).getAttribute("innerText");
                if (innerText.equals(type)){
                    img.findElement(By.xpath(".//div[@class='image-box']")).click();
                    upload(chromeDriver,attributeVo.getAttributeImageBlob().getImageUrl(),imageSet,"规格");
                }
            }
        }
        WebElement priceDiv = chromeDriver.findElement(By.xpath(".//div[@class='ant-table-wrapper price-inventory-table']"));
        List<WebElement> trs = priceDiv.findElements(By.xpath(".//tr[@class='ant-table-row ant-table-row-level-0']"));
        for (WebElement element:trs) {
            WebElement td = element.findElement(By.xpath(".//td[@class='ant-table-row-cell-break-word']"));
            WebElement span = td.findElements(By.xpath(".//span")).get(1);
            if (span.getAttribute("innerText").equals(type)){
                WebElement elementPrice = element.findElement(By.xpath(".//td[@class='fullPriceYuan ant-table-row-cell-break-word']"));
                List<WebElement> elements = elementPrice.findElements(By.xpath(".//input"));
                for (int i=0;i<elements.size();i++) {
                    if (i==0) {
                        elements.get(i).sendKeys(attributeVo.getGoodPrice().getMarketPrice().toString());
                    }
                    if (i==1){
                        elements.get(i).sendKeys(attributeVo.getGoodPrice().getAgreementPrice().toString());
                    }
                }
                WebElement elementStock = element.findElement(By.xpath(".//td[@class='stockQuantity ant-table-row-cell-break-word']"));
                String stock = "";
                if (Objects.isNull(attributeVo.getGoodPrice().getStock())){
                    stock = "1000";
                }else {
                    stock = attributeVo.getGoodPrice().getStock().toString();
                }
                elementStock.findElement(By.xpath(".//input")).sendKeys(stock);
            }
        }
    }

    public void upload(ChromeDriver chromeDriver,String url, Set<String> set, String label){
        String path = saveToFile(url);
        set.add(label);
        Integer count = 0;
        if (set.size()>0){
            count = set.size()-1;
        }
        String inputFile = "//input[@type='file' and @accept='.jpg,.png,.jpeg' and @style='display: none;']";
        wat(chromeDriver, By.xpath(inputFile));
        WebElement element = chromeDriver.findElement(By.xpath(inputFile));
        element.sendKeys(path);
        WebElement elementDiv = chromeDriver.findElements(By.xpath("//div[@class='ant-modal-content']")).get(count);
        WebElement elementImageDiv = elementDiv.findElement(By.xpath(".//div[@class='img-border']"));
        List<WebElement> elements = elementImageDiv.findElements(By.xpath(".//img[@class='img']"));
        elements.get(0).click();
        WebElement elementFoot = elementDiv.findElement(By.xpath(".//div[@class='ant-modal-footer']"));
        elementFoot.findElement(By.xpath(".//button[@class='ant-btn ant-btn-primary']")).click();
        deleteImage(path);
    }

    public static WebElement  isJudgingElement(WebElement webDriver,By by) {
        try {
            WebElement element = webDriver.findElement(by);
            return element;
        }catch (Exception e){
            return null;
        }
    }

    public static WebElement isJudgingElement(ChromeDriver webDriver,By by) {
        try {
            WebElement element = webDriver.findElement(by);
            return element;
        }catch (Exception e){
            return null;
        }
    }

    public void deleteImage(String path){
        File file = new File(path);
        file.delete();
    }

    public static void wat(ChromeDriver driver, By by) {
        new WebDriverWait(driver,3).until(
                ExpectedConditions.presenceOfElementLocated(by));
    }

    public static void wat(ChromeDriver driver,WebElement webElement,By by){
        new WebDriverWait(driver,3).until(ExpectedConditions.presenceOfNestedElementLocatedBy(webElement,by));
    }

    public void wait(ChromeDriver driver,By by){
        new WebDriverWait(driver,10).until(ExpectedConditions.elementToBeSelected(by));
    }

    public void waitV(ChromeDriver driver,By by){
        new WebDriverWait(driver,10).until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    public void waitEle(ChromeDriver driver,WebElement webElement){
        new WebDriverWait(driver,10).until(ExpectedConditions.elementToBeClickable(webElement));
    }

    public void waitClick(ChromeDriver driver,WebElement webElement){
        new WebDriverWait(driver,10).until(ExpectedConditions.elementToBeClickable(webElement));
    }

    public List<GoodAttribute> sortList(List<GoodAttribute> attributes,String label,Integer index){
        int indexPin = 0;
        for (int i=0;i<attributes.size();i++){
            if (attributes.get(i).getAttributeKey().equals(label)){
                indexPin = i;
            }
        }
        GoodAttribute attribute1 = attributes.get(indexPin);
        attributes.remove(indexPin);
        if (Objects.nonNull(index)) {
            attributes.add(index, attribute1);
        }else {
            attributes.add(attribute1);
        }
        return attributes;
    }

    public String deleteN(String str){
        if (str.contains("\n")){
            int i = str.indexOf("\n");
            StringBuffer stringBuffer = new StringBuffer(str);
            stringBuffer.deleteCharAt(i);
            return stringBuffer.toString();
        }
        return str;
    }

    @SneakyThrows
    public String saveToFile(String destUrl) {
        FileOutputStream fos = null;
        BufferedInputStream bis = null;
        HttpURLConnection httpUrl = null;
        URL url = null;
        int BUFFER_SIZE = 1024;
        byte[] buf = new byte[BUFFER_SIZE];
        int size = 0;
        String path = "";
        String s = UUID.randomUUID().toString();
        try {
            url = new URL(destUrl);
            httpUrl = (HttpURLConnection) url.openConnection();
            httpUrl.connect();
            bis = new BufferedInputStream(httpUrl.getInputStream());
            path = "/app/image/"+s+".png";
            byte[] streamBytes = getStreamBytes(bis);
            byte[] bytes = PicUtils.compressPicForScale(streamBytes, 100);
            fileToBytes(bytes,path);
        } catch (IOException e) {
        } catch (ClassCastException e) {
        } finally {
            try {
                bis.close();
                httpUrl.disconnect();
            } catch (IOException e) {
            } catch (NullPointerException e) {
            }
        }
        return path;
    }

    public byte[] getStreamBytes(InputStream is) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = is.read(buffer)) != -1) {
            baos.write(buffer, 0, len);
        }
        byte[] b = baos.toByteArray();
        is.close();
        baos.close();
        return b;
    }


    /**
     * 将Byte数组转换成文件
     * @param bytes byte数组
     * @param filePath 文件路径  如 D://test/ 最后“/”结尾
     */
    public void fileToBytes(byte[] bytes, String filePath) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            file = new File(filePath);
            if (!file.getParentFile().exists()){
                //文件夹不存在 生成
                boolean maked = file.getParentFile().mkdirs();
            }
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
