package com.dd.nio.controller;

import com.dd.nio.common.response.ResultData;
import com.dd.nio.service.MagicService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "tran")
@RestController
@RequestMapping("/magic/transform")
public class MagicController {

    @Autowired
    private MagicService magicService;

    @ApiOperation(value = "写入数据")
    @RequestMapping(value = "/write",method = RequestMethod.POST)
    public ResultData wirteForm(@RequestParam(value = "user_id") Long userId){
         return magicService.fillingGoods(userId);
    }

    @ApiOperation(value = "剩余条数")
    @RequestMapping(value = "/get-wirtes",method = RequestMethod.GET)
    public ResultData getWrite(){
        return magicService.getWrite();
    }
}
