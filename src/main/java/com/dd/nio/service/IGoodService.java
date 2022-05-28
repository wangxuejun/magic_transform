package com.dd.nio.service;

import com.dd.nio.entity.Good;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dd.nio.entity.vo.GoodVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author tao.wang15
 * @since 2022-05-26
 */
public interface IGoodService extends IService<Good> {

    List<GoodVo> getPageGoods();

    void deleteGood(Good good);

}
