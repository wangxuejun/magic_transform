package com.dd.nio.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dd.nio.common.exception.ServiceException;
import com.dd.nio.common.response.ResultData;
import com.dd.nio.common.utils.JwtUtils;
import com.dd.nio.common.utils.TimeUtils;
import com.dd.nio.entity.User;
import com.dd.nio.mapper.UserMapper;
import com.dd.nio.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author tao.wang15
 * @since 2022-05-30
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ResultData login(String userName, String password) {
        if (StringUtils.isEmpty(userName)){
            throw new ServiceException("用户名为空");
        }

        if (StringUtils.isEmpty(password)){
            throw new ServiceException("密码为空");
        }

        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUserName, userName)
                .eq(User::getPassword, password)
                .ge(User::getExprTime, TimeUtils.dateToString(new Date())));
        if (Objects.nonNull(user)){
            HashMap<String, Object> hashMap = Maps.newHashMap();
            hashMap.put("roles",user.getUserRole());
            String jwt = JwtUtils.createJwt(user.getId().toString(), userName, hashMap);
            return ResultData.success(jwt);
        }
        return ResultData.fail(401,"登陆失败");
    }

    @Override
    public ResultData listUser() {
        List<User> users = userMapper.selectList(new LambdaQueryWrapper<User>());
        return ResultData.success(users);
    }

    @Override
    public ResultData updateUser(User user) {
        userMapper.updateById(user);
        return ResultData.success();
    }

    @Override
    public ResultData addUser(User user) {
        List<User> users = userMapper.selectList(new LambdaQueryWrapper<User>().eq(User::getUserName, user.getUserName()));
        if (!users.isEmpty()){
            throw new ServiceException("用户名重复");
        }
        userMapper.insert(user);
        return ResultData.success();
    }


}
