package com.dd.nio.service.impl;

import com.dd.nio.common.operate.ChromeOperator;
import com.dd.nio.entity.Good;
import com.dd.nio.entity.GoodAttribute;
import com.dd.nio.entity.vo.GoodVo;
import com.dd.nio.service.IGoodService;
import com.dd.nio.service.MagicService;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class MagicSeerviceImpl implements MagicService {

    @Autowired
    private IGoodService goodService;

    @Autowired
    private ChromeOperator chromeOperator;


    public void fillingGoods() throws InterruptedException {
        ChromeDriver chromeDriver = chromeOperator.getChromeDriver();
        List<GoodVo> pageGoods = goodService.getPageGoods();
        chromeDriver.get("https://www.anhui.zcygov.cn/item-center-front/publishgoods/publish?categoryId=7903&spuId=100785469&protocolId=1028&bidId=80&instanceCode=AHWC");
        Thread.sleep(3000);
        for (GoodVo goodVo:pageGoods) {
            analyGood(chromeDriver,goodVo);
        }
    }

    public static void analyGood(ChromeDriver chromeDriver,GoodVo goodVo) throws InterruptedException {
        Good good = goodVo.getGood();
        List<GoodAttribute> attributes = goodVo.getAttributes();
        for (GoodAttribute attribute:attributes) {
            String attributeKey = attribute.getAttributeKey();
            WebElement elementByCssSelector = chromeDriver.findElementByCssSelector("label[title='" + attributeKey + "']");
            WebElement element = elementByCssSelector.findElement(By.xpath("../following-sibling::div"));
            String type = getType(element);
            if (type.equals("select")){
                fillingSelect(element,attribute.getAttributeValue(),chromeDriver);
            }else if (type.equals("form")){
                fillingForm(element);
            }else if (type.equals("checkBox")){
                fillingCheckbox(element,attribute.getAttributeValue());
            }else {
                fillingInput(element,attribute.getAttributeValue());
            }
        }
    }


    public static String getType(WebElement element1){
        //查找是否有select
        WebElement select = isJudgingElement(element1, By.xpath(".//div[@class='ant-select-selection\n" +
                "            ant-select-selection--single']"));
        if (Objects.nonNull(select)){
            return "select";
        }
        //如果有没有form表单 这种是form
        WebElement judgingElement = isJudgingElement(element1, By.xpath(".//form"));
        if (Objects.nonNull(judgingElement)){
            return "form";
        }
        WebElement checkBox = isJudgingElement(element1, By.xpath(".//input[@type='checkbox']"));
        if (Objects.nonNull(checkBox)){
            return "checkBox";
        }
        WebElement input = isJudgingElement(element1, By.xpath(".//input"));
        if (Objects.nonNull(input)){
            String type = input.getAttribute("type");
            if (StringUtils.isEmpty(type) || type.equals("text")){
                return "input";
            }
        }
        return null;
    }

    public static void fillingSelect(WebElement element,String brand,ChromeDriver chromeDriver) throws InterruptedException {
        WebElement elementDiv = chromeDriver.findElement(By.xpath("//ul[@class='ant-select-dropdown-menu  ant-select-dropdown-menu-root ant-select-dropdown-menu-vertical']"));
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
            if (webElement.getAttribute("innerText").equals("香山")){
                webElement.click();
            }
        }
    }

    public static void fillingInput(WebElement element,String input){
        WebElement element1 = element.findElement(By.xpath(".//input"));
        //判断input是否可以输入
        String disabled = element1.getAttribute("disabled");
        if (StringUtils.isNotEmpty(disabled)){
            System.out.println(element1.getAttribute("class"));
            element1.sendKeys(input);
        }
    }

    public static void fillingForm(WebElement element){
        WebElement element1 = element.findElement(By.xpath(".//form"));
        WebElement element2 = element1.findElement(By.xpath(".//input[@class='ant-radio-input']"));
        System.out.println(element2.getAttribute("class"));
        element2.click();
        WebElement element3 = element1.findElement(By.xpath(".//span[@class='zcy-tenant-addressCode-undefined ant-cascader-picker zcy-tenant-addressCode-undefined']"));
        element3.click();
        //todo
    }

    public static void fillingCheckbox(WebElement element,String box){
        List<String> list = Arrays.asList(box.split(","));
        List<WebElement> elements = element.findElements(By.xpath(".//input[@type='checkbox']"));
        if (!elements.isEmpty()){
            String disabled = elements.get(0).getAttribute("disabled");
            if (StringUtils.isNotEmpty(disabled)){
                return;
            }
        }
        for (WebElement webElement:elements) {
            Boolean isClick = false;
            for (String str:list) {
                if (str.equals(webElement.getAttribute("value"))){
                    webElement.click();
                    isClick = true;
                }
            }
            if (!isClick){
                //尝试查找是否有'点击添加字段'
                WebElement isAddElement = isJudgingElement(element, By.xpath(".//a[text()='点击添加']"));
                if (Objects.nonNull(isAddElement)){
                    isAddElement.click();
                }else {
                    //判断是否必填
                    elements.get(0).click();
                }
            }
        }
    }

    public static void fillingGoodsTypeImage(WebElement element,ChromeDriver chromeDriver) {
        //查询勾选的checkbox 的index
        WebElement elementByCssSelector = chromeDriver.findElementByCssSelector("label[title='标准规格']");
        WebElement elementImage = elementByCssSelector.findElement(By.xpath("../following-sibling::div"));
        List<WebElement> elements = elementImage.findElements(By.xpath(".//span[@class='ant-checkbox ant-checkbox-checked']"));
        List<WebElement> webElements = element.findElements(By.xpath(".//div[@class='image-box']"));
        for (int i=0;i<elements.size();i++) {
            WebElement webElement = webElements.get(i);
            webElement.click();
            upload(chromeDriver,"");
        }
    }

    public static void upload(ChromeDriver chromeDriver,String path){
        WebElement element = chromeDriver.findElement(By.xpath("//input[@type='file']"));
        element.sendKeys(path);
        WebElement elementDiv = chromeDriver.findElement(By.xpath("//div[@class='ant-modal salesUploadImageModal']"));
        WebElement elementImageDiv = elementDiv.findElement(By.xpath(".//div[@class='img-border']"));
        List<WebElement> elements = elementImageDiv.findElements(By.xpath(".//img[@class='img']"));
        elements.get(0).click();
        WebElement elementFoot = elementDiv.findElement(By.xpath(".//div[@class='ant-modal-footer']"));
        elementFoot.findElement(By.xpath(".//button[@class='ant-btn ant-btn-primary']")).click();
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
