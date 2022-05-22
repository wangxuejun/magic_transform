package com.dd.nio;


import com.alibaba.fastjson.JSONObject;
import com.dd.nio.common.client.HuiClient;
import com.dd.nio.common.client.response.Category;
import com.dd.nio.common.client.response.ManagerData;
import com.dd.nio.common.client.response.ManagerResponse;
import com.dd.nio.common.client.response.WareHouse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * Unit test for simple App.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@SuppressWarnings({"FieldCanBeLocal", "SpringJavaAutowiredMembersInspection"})
public class AppTest 
{

    @Autowired
    private HuiClient huiClient;

    @Test
    public void shouldAnswerWithTrue() {
//        ManagerData managerData = huiClient.getManageIds();
//        System.out.println(managerData);
//        List<Category> categoryId = huiClient.getCategoryId(managerData.getId(), managerData.getBids().get(0).getId(), 0L);
//        System.out.println(categoryId);
        String wareHouse = huiClient.getWareHouse();
        System.out.println(wareHouse);
    }
}
