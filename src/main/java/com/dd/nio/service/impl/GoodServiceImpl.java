package com.dd.nio.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dd.nio.entity.Good;
import com.dd.nio.entity.GoodAttribute;
import com.dd.nio.entity.vo.GoodVo;
import com.dd.nio.mapper.GoodMapper;
import com.dd.nio.service.IGoodAttributeService;
import com.dd.nio.service.IGoodService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author tao.wang15
 * @since 2022-05-26
 */
@Service
public class GoodServiceImpl extends ServiceImpl<GoodMapper, Good> implements IGoodService {

    @Autowired
    @SuppressWarnings("all")
    private GoodMapper goodMapper;

    @Autowired
    private IGoodAttributeService goodAttributeService;

    @Override
    public List<GoodVo> getPageGoods() {
        List<GoodVo> res = Lists.newArrayList();
        List<Good> goods = goodMapper.selectList(new LambdaQueryWrapper<Good>()
                .eq(Good::getIsEffect, 1));
        goods.stream().forEach(record -> {
            List<GoodAttribute> list = goodAttributeService.list(new LambdaQueryWrapper<GoodAttribute>().eq(GoodAttribute::getGoodId, record.getId()));
            GoodVo goodVo = new GoodVo();
            goodVo.setGood(record);
            goodVo.setAttributes(list);
            res.add(goodVo);
        });
        return res;
    }

    @Override
    public void deleteGood(Good good) {
        good.setIsEffect(0);
        goodMapper.updateById(good);
    }

    @Override
    public void complete(Good good) {
        goodMapper.deleteById(good);
    }

    @Override
    public Integer getWriteCount() {
        Integer integer = goodMapper.selectCount(new LambdaQueryWrapper<Good>().eq(Good::getIsEffect, 1));
        return integer;
    }

}
