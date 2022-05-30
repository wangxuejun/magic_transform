package com.dd.nio.controller;

import com.dd.nio.common.api.JwtIgnore;
import com.dd.nio.common.response.ResultData;
import com.dd.nio.service.IUserService;
import com.dd.nio.service.MagicService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/magic/transform")
public class MagicController {

    @Autowired
    private MagicService magicService;

    @SneakyThrows
    @JwtIgnore
    @RequestMapping(value = "/write",method = RequestMethod.POST)
    public ResultData wirteForm(){
        return magicService.fillingGoods();
    }
}
