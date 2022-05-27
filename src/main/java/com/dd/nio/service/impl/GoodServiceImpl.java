package com.dd.nio.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

import java.util.ArrayList;
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
        Page<Good> goodPage = goodMapper.selectPage(new Page<>(1, 10), new LambdaQueryWrapper<Good>());
        List<Good> records = goodPage.getRecords();
        records.stream().forEach(record -> {
            List<GoodAttribute> list = goodAttributeService.list(new LambdaQueryWrapper<GoodAttribute>().eq(GoodAttribute::getGoodId, record.getId()));
            GoodVo goodVo = new GoodVo();
            goodVo.setGood(record);
            goodVo.setAttributes(list);
            res.add(goodVo);
        });
        return res;
    }

}
