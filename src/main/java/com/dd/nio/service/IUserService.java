package com.dd.nio.service;

import com.dd.nio.common.response.ResultData;
import com.dd.nio.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author tao.wang15
 * @since 2022-05-30
 */
public interface IUserService extends IService<User> {

    ResultData login(String userName, String password);

    ResultData listUser(Integer pageNo,Integer pageeSize);

    ResultData updateUser(User user);

    ResultData addUser(User user);

    ResultData del(Integer userId);
}
