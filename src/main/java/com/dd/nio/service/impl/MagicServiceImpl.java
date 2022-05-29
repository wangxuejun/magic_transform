package com.dd.nio.service.impl;

import com.dd.nio.common.operate.ChromeOperator;
import com.dd.nio.entity.Good;
import com.dd.nio.entity.GoodAttribute;
import com.dd.nio.entity.GoodImage;
import com.dd.nio.entity.GoodPrice;
import com.dd.nio.entity.vo.AttributeTypeVo;
import com.dd.nio.entity.vo.GoodVo;
import com.dd.nio.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Slf4j
@Service
public class MagicServiceImpl implements MagicService {

    @Autowired
    private IGoodService goodService;

    @Autowired
    private ChromeOperator chromeOperator;

    @Autowired
    private IAttributeTypeService iAttributeTypeService;

    @Autowired
    private IGoodPriceService iGoodPriceService;

    @Autowired
    private IGoodImageService iGoodImageService;


    @Override
    public void fillingGoods() throws InterruptedException {
        ChromeDriver chromeDriver = chromeOperator.getChromeDriver();
        List<GoodVo> pageGoods = goodService.getPageGoods();
        chromeDriver.get("https://www.anhui.zcygov.cn/item-center-front/releaseGoods?_app_=zcy.agreement");
        wat(chromeDriver,By.xpath("//div[@class='list-header'][1]"));
        Thread.sleep(1000);
        for (GoodVo goodVo:pageGoods) {
            int count = 1;
            List<String> list = Arrays.asList(goodVo.getGood().getCategoryString().split(","));
            if (ChooseCategory(chromeDriver,count,list.get(0),"一级类目")){
                count ++;
                Thread.sleep(1000);
                if (ChooseCategory(chromeDriver,count,list.get(1),"二级类目")){
                    count ++;
                    Thread.sleep(1000);
                    if (ChooseCategory(chromeDriver,count,list.get(2),"三级类目")){
                        Thread.sleep(1000);
                        WebElement judgingElement = isJudgingElement(chromeDriver, By.xpath("//div[@class='spu']"));
                        if (Objects.nonNull(judgingElement)){
                            //获取品牌型号
                            List<GoodAttribute> attributes = goodVo.getAttributes();
                            String brand = ""; String type= "";
                            for (GoodAttribute attribute:attributes){
                                if (attribute.getAttributeKey().equals("品牌")){
                                    brand = attribute.getAttributeValue();
                                }
                                if (attribute.getAttributeKey().equals("型号")){
                                    type = attribute.getAttributeValue();
                                }
                            }
                            if (StringUtils.isNotEmpty(brand) && StringUtils.isNotEmpty(type)){
                                Choose(judgingElement,brand,type);
                            }else {
                                //删除数据
                                goodService.deleteGood(goodVo.getGood());
                                break;
                            }
                        }else {
                            chromeDriver.findElement(By.xpath("button[@class='ant-btn ant-btn-primary']")).click();
                        }
                    }
                }else {
                    //删除数据
                    goodService.deleteGood(goodVo.getGood());
                    break;
                }
            }else {
                //删除数据
                goodService.deleteGood(goodVo.getGood());
                break;
            }
            try {
                analyGood(chromeDriver,goodVo);
            }catch (Exception e){
                log.error(e.getMessage());
                log.error(goodVo.getGood().getName());
                log.error(goodVo.getGood().getId().toString());
                goodService.deleteGood(goodVo.getGood());
            }
        }
    }

    public void Choose(WebElement element,String brand,String type){
        WebElement element1 = element.findElement(By.xpath(".//div[@class='ant-list spu-list ant-list-vertical ant-list-split']"));
        List<WebElement> elements = element1.findElements(By.xpath(".//div[@class='ant-list-item no-active']"));
        for (WebElement webElement:elements){
            WebElement span = webElement.findElement(By.xpath(".//div/div/div/span"));
            if (span.getAttribute("innerText").equals(brand)){
                webElement.click();
            }
        }
        WebElement element1Type = element.findElement(By.xpath(".//div[@class='ant-list spu-list specification ant-list-vertical ant-list-split']"));
        List<WebElement> elementsType = element1Type.findElements(By.xpath(".//div[@class='ant-list-item no-active']"));
        for (WebElement webElement:elementsType){
            WebElement span = webElement.findElement(By.xpath(".//div/span/span[@class='category-item']"));
            if (span.getAttribute("innerText").equals(type)){
                webElement.click();
            }
        }
    }

    public Boolean ChooseCategory(ChromeDriver chromeDriver,Integer count,String categoryStr,String level){
        String path = "//div[@class='list-header']["+count+"]";
        wat(chromeDriver,By.xpath(path));
        List<WebElement> elements = chromeDriver.findElements(By.xpath("//div[@class='ant-list category-list ant-list-vertical ant-list-split']"));
        WebElement elementListOne = chromeDriver.findElement(By.xpath("//div[@class='list-header']["+count+"]"));
        if (elementListOne.getAttribute("innerText").equals(level)){
            WebElement element = elementListOne.findElement(By.xpath("../../following-sibling::div"));
            System.out.println(element.getAttribute("class"));
            List<WebElement> elementTexts = element.findElements(By.xpath(".//div[@class='ant-list-item no-active']"));
            Boolean isClick = false;
            for (WebElement webElement:elementTexts) {
                WebElement judgingElement = isJudgingElement(webElement, By.xpath(".//span[@class='category-item']"));
                if (Objects.nonNull(judgingElement)){
                    if (judgingElement.getAttribute("innerText").equals(categoryStr)){
                        webElement.click();
                        isClick = true;
                        break;
                    }
                }
            }
            if (isClick){
                List<WebElement> elementsLast = chromeDriver.findElements(By.xpath("//div[@class='ant-list category-list ant-list-vertical ant-list-split']"));
                if (count<3) {
                    if (elements.size() == elementsLast.size()) {
                        return false;
                    } else {
                        System.out.println(level+"yyy");
                        return true;
                    }
                }else {
                    return true;
                }
            }else {
                return false;
            }
        }
        return false;
    }

    public void analyGood(ChromeDriver chromeDriver,GoodVo goodVo) throws InterruptedException {
        Good good = goodVo.getGood();
        List<GoodAttribute> attributes = goodVo.getAttributes();
        Set<String> selectSet = new HashSet<>();
        Set<String> imageSet = new HashSet<>();
        Set<String> checkSet = new HashSet<>();
        selectSet.add(UUID.randomUUID().toString());
        imageSet.add(UUID.randomUUID().toString());
        checkSet.add(UUID.randomUUID().toString());
        Integer count =0;
        for (GoodAttribute attribute:attributes) {
            String attributeKey = attribute.getAttributeKey();
            if (attributeKey.equals("规格")
                    || attributeKey.equals("价格")
                    || attributeKey.equals("产品图片")
                    || attributeKey.equals("产品详情")
                    || attributeKey.equals("运费模版")){
                fillingSpecial(attributeKey,chromeDriver,attribute.getAttributeValue(),imageSet,count,selectSet);
                continue;
            }
            WebElement elementByCssSelector = chromeDriver.findElementByCssSelector("label[title='" + attributeKey + "']");
            WebElement element = elementByCssSelector.findElement(By.xpath("../following-sibling::div"));
            String type = getType(element);
            if (type.equals("select")){
                fillingSelect(element,attribute.getAttributeValue(),chromeDriver,selectSet,attributeKey);
            }else if (type.equals("form")){
                fillingForm(element,attribute.getAttributeValue(),chromeDriver);
            }else if (type.equals("checkBox")){
                fillingCheckbox(element,attribute.getAttributeValue(),chromeDriver,checkSet,attributeKey);
            }else {
                fillingInput(element,attribute.getAttributeValue());
            }
        }
    }

    private void fillingSpecial(String attributeKey,ChromeDriver chromeDriver, String attributeValue,Set<String> imageSet,Integer count,Set<String> selectSet) {
        if (attributeKey.equals("规格")){
            //查询规格数据
            AttributeTypeVo attributeVo = iAttributeTypeService.getAttributeVo(Long.valueOf(attributeValue));
            //查找销售规格
            fillingGoodAttribute(chromeDriver,attributeVo.getAttributeType().getType(),attributeVo,imageSet,selectSet);
        }
        if (attributeKey.equals("价格")){
            //查询价格数据
            GoodPrice price = iGoodPriceService.getById(Long.valueOf(attributeValue));
            filingPrice(chromeDriver,price);
        }
        if (attributeKey.equals("产品图片")){
            count ++ ;
            GoodImage image = iGoodImageService.getById(Long.valueOf(attributeValue));
            fillingImage(chromeDriver,image,imageSet,count,"产品图片");
        }
        if (attributeKey.equals("产品详情")){
            fillingGoodDetail(chromeDriver,attributeValue);
        }
        if (attributeKey.equals("运费模版")){
            fillingMoney(chromeDriver,selectSet,"运费模版");
        }
    }

    private void fillingMoney(ChromeDriver chromeDriver,Set<String> selectSet,String label) {
        WebElement elementByCssSelector = chromeDriver.findElementByCssSelector("label[title='运费模版']");
        WebElement element = elementByCssSelector.findElement(By.xpath("../following-sibling::div"));
        fillingSelect(element,"华东",chromeDriver,selectSet,label);
    }

    private void fillingGoodDetail(ChromeDriver chromeDriver, String attributeValue) {
        WebElement elementByCssSelector = chromeDriver.findElementByCssSelector("label[title='产品详情']");
        WebElement element = elementByCssSelector.findElement(By.xpath("../following-sibling::div"));
        WebElement frame = element.findElement(By.xpath(".//iframe"));
        frame.findElement(By.xpath(".//body/p")).sendKeys(attributeValue);
    }

    private void fillingImage(ChromeDriver chromeDriver, GoodImage image, Set<String> imageSet,Integer count,String label) {
        WebElement element = chromeDriver.findElement(By.xpath("//div[@class='uploadImage']"));
        List<WebElement> elements = element.findElements(By.xpath(".//div[@class='image-upload']"));
        WebElement webElement = elements.get(count);
        webElement.findElement(By.xpath(".//div[@class='image-box']")).click();
        upload(chromeDriver,image.getImageBin(),imageSet,label);
        count ++;
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
                elements.get(i).sendKeys(price.getChannelPrice().toString());
            }
        }
        WebElement elementStock = element.findElement(By.xpath(".//td[@class='stockQuantity ant-table-row-cell-break-word']"));
        elementStock.findElement(By.xpath(".//input")).sendKeys("9999");
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

    public static void fillingSelect(WebElement element,String brand,ChromeDriver chromeDriver,Set<String> selectSet,String label) {
        element.findElement(By.xpath(".//div[@class='ant-select-selection\n" +
                "            ant-select-selection--single']")).click();
        selectSet.add(label);
        WebElement elementDiv = chromeDriver.findElement(By.xpath("//ul[@class='ant-select-dropdown-menu  ant-select-dropdown-menu-root ant-select-dropdown-menu-vertical']["+selectSet.size()+"]"));
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

    public static void fillingInput(WebElement element,String input){
        WebElement element1 = element.findElement(By.xpath(".//input"));
        //判断input是否可以输入
        String disabled = element1.getAttribute("disabled");
        if (StringUtils.isNotEmpty(disabled)){
            System.out.println(element1.getAttribute("class"));
            element1.sendKeys(input);
        }
    }

    public static void fillingForm(WebElement element,String productPlace,ChromeDriver chromeDriver){
        List<String> list = Arrays.asList(productPlace.split(" "));
        WebElement element1 = element.findElement(By.xpath(".//form"));
        WebElement element2 = element1.findElement(By.xpath(".//input[@class='ant-radio-input']"));
        System.out.println(element2.getAttribute("class"));
        element2.click();
        WebElement element3 = element1.findElement(By.xpath(".//span[@class='zcy-tenant-addressCode-undefined ant-cascader-picker zcy-tenant-addressCode-undefined']"));
        element3.click();
        //todo
        WebElement element4 = chromeDriver.findElement(By.xpath("//ul[@class='ant-cascader-menu'][0]"));
        List<WebElement> elements = element4.findElements(By.xpath(".//li"));
        for (WebElement webElement:elements){
            if (webElement.getAttribute("innerText").equals(list.get(0))){
                webElement.click();
            }
        }
        WebElement element5 = chromeDriver.findElement(By.xpath("//ul[@class='ant-cascader-menu'][1]"));
        List<WebElement> elements1 = element5.findElements(By.xpath(".//li"));
        for (WebElement webElement:elements1){
            if (webElement.getAttribute("innerText").equals(list.get(1))){
                webElement.click();
            }
        }
        WebElement element6 = chromeDriver.findElement(By.xpath("//ul[@class='ant-cascader-menu'][2]"));
        List<WebElement> elements2 = element6.findElements(By.xpath(".//li"));
        for (WebElement webElement:elements2){
            if (webElement.getAttribute("innerText").equals(list.get(1))){
                webElement.click();
            }
        }
    }

    public static void fillingCheckbox(WebElement element,String box,ChromeDriver chromeDriver,Set<String> checkSet,String label){
        List<WebElement> elements = element.findElements(By.xpath(".//input[@type='checkbox']"));
        if (!elements.isEmpty()){
            String disabled = elements.get(0).getAttribute("disabled");
            if (StringUtils.isNotEmpty(disabled)){
                return;
            }
        }
            Boolean isClick = false;
            for (WebElement webElement:elements){
                if (webElement.getAttribute("vaule").equals(box)){
                    webElement.click();
                    isClick=true;
                }
            }
            if (!isClick){
                //尝试查找是否有'点击添加字段'
                WebElement isAddElement = isJudgingElement(element, By.xpath(".//a[text()='点击添加']"));
                checkSet.add(label);
                if (Objects.nonNull(isAddElement)){
                    isAddElement.click();
                    WebElement element1 = chromeDriver.findElement(By.xpath("//div[@class='ant-popover ant-popover-placement-bottom']["+checkSet.size()+"]"));
                    WebElement element2 = element1.findElement(By.xpath(".//input"));
                    element2.sendKeys(box);
                    element1.findElement(By.xpath(".//button[@class='ant-btn ant-btn-primary ant-btn-sm']")).click();
                }
            }
    }
    public void fillingGoodAttribute(ChromeDriver chromeDriver,String type,AttributeTypeVo attributeVo,Set<String> imageSet,Set<String> checkSet){
        //查询勾选的checkbox 的index
        WebElement elementGui = chromeDriver.findElement(By.xpath("//span[text()='销售规格']"));
        WebElement elementImage = elementGui.findElement(By.xpath("../following-sibling::form"));
        //先判断是否必填
        WebElement label = elementImage.findElement(By.xpath(".//div/div/div/div/div/label"));
        List<WebElement> elements = elementImage.findElements(By.xpath(".//input[@type='checkbox']"));
        Boolean isClick = false;
        for (WebElement webElement:elements){
            if (webElement.getAttribute("value").equals(type)){
                webElement.click();
                isClick = true;
            }
        }
        if (label.getAttribute("class").equals("ant-form-item-required") && !isClick){
            elements.get(0).click();
        }else if (!isClick){
            //尝试查找是否有'点击添加字段'
            WebElement isAddElement = isJudgingElement(elementImage, By.xpath(".//a[text()='点击添加']"));
            checkSet.add("规格");
            if (Objects.nonNull(isAddElement)){
                isAddElement.click();
                WebElement element1 = chromeDriver.findElement(By.xpath("//div[@class='ant-popover ant-popover-placement-bottom']["+checkSet.size()+"]"));
                WebElement element2 = element1.findElement(By.xpath(".//input"));
                element2.sendKeys(type);
                element1.findElement(By.xpath(".//button[@class='ant-btn ant-btn-primary ant-btn-sm']")).click();
            }
            List<WebElement> elementsNew = elementImage.findElements(By.xpath(".//input[@type='checkbox']"));
            elementsNew.get(elementsNew.size()-1).click();
        }
        if (Objects.nonNull(attributeVo.getAttributeImageBlob())){
            WebElement imageEle = chromeDriver.findElement(By.xpath("//label[@title='规格图片']"));
            WebElement webElement = imageEle.findElement(By.xpath("../following-sibling::div"));
            List<WebElement> elementsImgs = webElement.findElements(By.xpath(".//div[@class='image-upload']"));
            Boolean isUpload = false;
            for (WebElement img:elementsImgs){
                String innerText = img.findElement(By.xpath(".//div[@class='image-title']")).getAttribute("innerText");
                if (innerText.equals(type)){
                    img.findElement(By.xpath(".//div[@class='image-box']")).click();
                    upload(chromeDriver,attributeVo.getAttributeImageBlob().getImageBlob(),imageSet,"规格");
                    isUpload = true;
                }
            }
            if (!isUpload){
                for (WebElement img:elementsImgs){
                    WebElement judgingElement = isJudgingElement(imageEle, By.xpath(".//div[@class='image-box']"));
                    if (Objects.nonNull(judgingElement)){
                        judgingElement.click();
                        upload(chromeDriver,attributeVo.getAttributeImageBlob().getImageBlob(),imageSet,"规格");
                        break;
                    }
                }
            }
        }
        List<WebElement> isClickEle = elementImage.findElements(By.xpath(".//span[@class='ant-checkbox ant-checkbox-checked']"));
        for (WebElement clickEle:isClickEle) {
            String value = clickEle.findElement(By.xpath(".//input")).getAttribute("value");
            //填充价格
            WebElement tableEle = chromeDriver.findElement(By.xpath("//div[@class='ant-table ant-table-middle ant-table-bordered ant-table-scroll-position-left']"));
            WebElement trEle = tableEle.findElement(By.xpath(".//tbody/tr"));
            List<WebElement> tdEles = trEle.findElements(By.xpath(".//td"));
            if (tdEles.get(0).findElement(By.xpath(".//span[2]")).getAttribute("innerText").equals(value)) {
                for (WebElement element : tdEles) {
                    if (element.getAttribute("class").equals("fullPriceYuan ant-table-row-cell-break-word")) {
                        List<WebElement> priceEle = element.findElements(By.xpath(".//div/div[@class='ant-col-14 ant-form-item-control-wrapper']"));
                        for (int i = 0; i < priceEle.size(); i++) {
                            WebElement element1 = priceEle.get(i).findElement(By.xpath(".//input[@class='ant-input-number-input']"));
                            if (i == 0) {
                                element1.sendKeys(attributeVo.getGoodPrice().getMarketPrice().toString());
                            }
                            if (i == 1) {
                                element1.sendKeys(attributeVo.getGoodPrice().getChannelPrice().toString());
                            }
                        }
                    }
                    if (element.getAttribute("class").equals("stockQuantity ant-table-row-cell-break-word")) {
                        element.findElement(By.xpath(".//input")).sendKeys("9999");
                    }
                    if (element.getAttribute("class").equals("otherInfo ant-table-row-cell-break-word")) {
                        List<WebElement> elementsHref = element.findElements(By.xpath(".//div[@class='ant-row ant-form-item']"));
                        if (elementsHref.size() > 1) {
                            for (WebElement webElement1 : elementsHref) {
                                WebElement judgingElement = isJudgingElement(webElement1, By.xpath(".//span[@class='ant-form-item-required']"));
                                if (Objects.nonNull(judgingElement)) {
                                    webElement1.findElement(By.xpath(".//input")).sendKeys(attributeVo.getAttributeType().getAttributeHref());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void upload(ChromeDriver chromeDriver, Blob blob, Set<String> set, String label){
        set.add(label);
        String path = writeImage(blob);
        WebElement element = chromeDriver.findElement(By.xpath("//input[@type='file']["+set.size()+"]"));
        element.sendKeys(path);
        WebElement elementDiv = chromeDriver.findElement(By.xpath("//div[@class='ant-modal salesUploadImageModal']"));
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

    public static WebElement  isJudgingElement(ChromeDriver webDriver,By by) {
        try {
            WebElement element = webDriver.findElement(by);
            return element;
        }catch (Exception e){
            return null;
        }
    }

    public String writeImage(Blob blob){
        InputStream is = null;
        FileOutputStream os = null;
        String path = "src/main/resources/" + UUID.randomUUID()+".png";
        try {
            is = blob.getBinaryStream();
            os = new FileOutputStream(path);
            byte[] arr = new byte[1024];
            int n = is.read(arr);
            while (n != -1) {
                os.write(arr, 0, n);
                n = is.read(arr);
            }
            return path;
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                is.close();
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void deleteImage(String path){
        File file = new File(path);
        file.delete();
    }

    public void wat(ChromeDriver driver,By by) {
//        Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
//                .withTimeout(30, TimeUnit.SECONDS)
//                .pollingEvery(8, TimeUnit.SECONDS)
//                .ignoring(NoSuchElementException.class);
//        WebElement clickseleniumlink = wait.until(new Function<WebDriver, WebElement>() {
//            @Override
//            public WebElement apply(WebDriver driver) {
//                return driver.findElement(by);
//            }
//        });
        new WebDriverWait(driver,10).until(
                ExpectedConditions.presenceOfElementLocated(By.cssSelector("css locator")));
    }
}
