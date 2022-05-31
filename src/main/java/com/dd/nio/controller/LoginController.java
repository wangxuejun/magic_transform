package com.dd.nio.controller;

import com.dd.nio.common.api.JwtIgnore;
import com.dd.nio.common.response.ResultData;
import com.dd.nio.entity.User;
import com.dd.nio.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "user")
@RestController
@RequestMapping("magic/user")
public class LoginController {

    @Autowired
    private IUserService iUserService;

    @JwtIgnore
    @ApiOperation(value = "登陆")
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public ResultData login(@RequestParam(value = "user_name") String userName,
                            @RequestParam(value = "password") String password){
       return iUserService.login(userName,password);
    }

    @ApiOperation(value = "添加用户")
    @RequestMapping(value = "/add",method = RequestMethod.POST,name="admin")
    public ResultData addUser(@RequestBody User user){
        return iUserService.addUser(user);
    }

    @ApiOperation(value = "更新用户")
    @RequestMapping(value = "/update",method = RequestMethod.POST,name="admin")
    public ResultData updateUser(@RequestBody User user){
        return iUserService.updateUser(user);
    }

    @ApiOperation(value = "用户列表")
    @RequestMapping(value = "/list",method = RequestMethod.GET,name="admin")
    public ResultData list(){
        return iUserService.listUser();
    }

}
