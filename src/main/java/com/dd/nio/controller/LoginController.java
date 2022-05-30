package com.dd.nio.controller;

import com.dd.nio.common.api.JwtIgnore;
import com.dd.nio.common.response.ResultData;
import com.dd.nio.entity.User;
import com.dd.nio.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/magic/user")
public class LoginController {

    @Autowired
    private IUserService iUserService;

    @JwtIgnore
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public ResultData login(@RequestParam(value = "userName") String userName,
                            @RequestParam(value = "password") String password){
       return iUserService.login(userName,password);
    }

    @RequestMapping(value = "/add",method = RequestMethod.POST,name="admin")
    public ResultData addUser(@RequestBody User user){
        return iUserService.addUser(user);
    }

    @RequestMapping(value = "/update",method = RequestMethod.POST,name="admin")
    public ResultData updateUser(@RequestBody User user){
        return iUserService.updateUser(user);
    }

    @RequestMapping(value = "/list",method = RequestMethod.POST,name="admin")
    public ResultData list(){
        return iUserService.listUser();
    }

}
