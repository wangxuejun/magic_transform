package com.dd.nio.common.client;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dd.nio.common.client.response.Category;
import com.dd.nio.common.client.response.ManagerData;
import com.dd.nio.common.client.response.ManagerResponse;
import com.dd.nio.common.client.response.WareHouse;
import com.dd.nio.common.operate.ChromeOperator;
import com.dd.nio.common.utils.JSONUtils;
import org.openqa.selenium.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URLDecoder;
import java.util.List;


public class HuiClient {

    private static final String MANAGERURL = "https://www.anhui.zcygov.cn/api/biz-item/item-platform/manages/agreement/list";

    private static final String GETCATEEGORY = "https://www.anhui.zcygov.cn/api/micro/category/backCategories/categoriesByLayer/cacheUpdate/common";

    private static final String GETWAREHOUSE = "https://www.anhui.zcygov.cn/api/micro/warehouse/findAllValidWarehouses";

    @Autowired
    private ChromeOperator chromeOperator;

    private RestTemplate restTemplate;


    public HuiClient() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * https://www.anhui.zcygov.cn/api/biz-item/item-platform/manages/agreement/list -> 获取pid POST（{"pageSize":1,"pageNo":1,"state":1}）
     * 获取 id 、bids[id]、supplierCode、instanceCode、supplierCode
     */
    public ManagerData getManageIds(){
        JSONObject param = new JSONObject();
        param.put("pageSize",1);
        param.put("pageNo",1);
        param.put("state",1);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("Accept", "application/json");
        httpHeaders.set("Cookie",chromeOperator.getCookie());
        HttpEntity httpEntity = new HttpEntity<>(param,httpHeaders);
        JSONObject jsonObject = restTemplate.postForObject(MANAGERURL, httpEntity, JSONObject.class);
        JSONObject result = jsonObject.getJSONObject("result");
        JSONArray data = result.getJSONArray("data");
        JSONObject jsonObject1 = data.getJSONObject(0);
        String s = jsonObject1.toJSONString();
        ManagerData managerData = JSONUtils.parseObject(s, ManagerData.class);
        return managerData;
    }


    /**
     * https://www.anhui.zcygov.cn/api/micro/category/agCategoryTree?timestamp=1652976771&protocolId=1028&bidId=80&pid=0&onlyAuthed=true&needQualificationMark=true
     * https://www.anhui.zcygov.cn/api/micro/category/backCategories/categoriesByLayer/cacheUpdate/common?timestamp=1652855535&pid=0&needQualificationMark=true
     * 获取类别id
     */
    public List<Category> getCategoryId(Long protocolId,Long bidId,Long pid){
        //设置请求头参数
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", chromeOperator.getCookie());
        HttpEntity request = new HttpEntity(requestHeaders);
        String unixTime = Long.toString(System.currentTimeMillis()/1000L);
        String url = GETWAREHOUSE + "?" + "timestamp="+ unixTime + "&protocolId="+ protocolId + "&bidId="+bidId+"&pid="+pid+"&onlyAuthed=true&needQualificationMark=true";
        ResponseEntity<JSONObject> anotherExchange = restTemplate.exchange(url , HttpMethod.GET,request, JSONObject.class);
        JSONObject body = anotherExchange.getBody();
        JSONArray result = body.getJSONArray("result");
        String s = result.toJSONString();
        List<Category> categories = JSONUtils.toList(s, Category.class);
        return categories;
    }



    /**
     * https://www.anhui.zcygov.cn/api/micro/warehouse/findAllValidWarehouses?timestamp=1652884176
     * 获取warehouse
     */
        public String getWareHouse(){
            //设置请求头参数
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.add("Cookie", chromeOperator.getCookie());
            HttpEntity request = new HttpEntity(requestHeaders);
            String unixTime = Long.toString(System.currentTimeMillis()/1000L);
            String url = GETWAREHOUSE + "?" + "timestamp="+ unixTime;
            ResponseEntity<JSONArray> anotherExchange = restTemplate.exchange(url , HttpMethod.GET,request, JSONArray.class);
            JSONArray body = anotherExchange.getBody();
            JSONObject jsonObject = body.getJSONObject(0);
            String code = jsonObject.getString("code");
            return code;
        }

    /**
     *https://www.anhui.zcygov.cn/api/micro/transTemplate/getTransTemplatesBySupplierOrgId?timestamp=1652898604&pageNo=1&pageSize=20&name=
     * 获取 transExpensesItemDTO id 快递id
     */


    /**
     * https://www.anhui.zcygov.cn/api/micro/item/renderPublishPage/router?timestamp=1652898603&protocolId=1028&layer=128&flag=1&categoryId=8493&isNewBusiness=true
     * 获取必填表单树形
     */


    /**
     * https://www.anhui.zcygov.cn/api/goods/draft/ag/create
     * 创建为草稿接口
     */

    public static void main(String[] args) {
        String url = "https://middle.anhui.zcygov.cn/user/apps/getAppsBasicInfo?timestamp=1652949298&path=%2Fdashboard%2Fpanel";
        String decode = URLDecoder.decode(url);
        RestTemplate restTemplate = new RestTemplate();
        Object forObject = restTemplate.getForObject(decode, Object.class);
        System.out.println(forObject);

    }



}
