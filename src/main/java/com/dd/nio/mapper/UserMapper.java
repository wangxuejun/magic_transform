package com.dd.nio.mapper;

import com.dd.nio.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author tao.wang15
 * @since 2022-05-30
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
