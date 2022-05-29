package com.dd.nio.service.impl;

import com.dd.nio.entity.AttributeImageBlob;
import com.dd.nio.entity.AttributeType;
import com.dd.nio.entity.GoodPrice;
import com.dd.nio.entity.vo.AttributeTypeVo;
import com.dd.nio.mapper.AttributeTypeMapper;
import com.dd.nio.service.IAttributeImageBlobService;
import com.dd.nio.service.IAttributeTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dd.nio.service.IGoodPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author tao.wang15
 * @since 2022-05-27
 */
@Service
public class AttributeTypeServiceImpl extends ServiceImpl<AttributeTypeMapper, AttributeType> implements IAttributeTypeService {

    @Autowired
    @SuppressWarnings("all")
    private AttributeTypeMapper attributeTypeMapper;

    @Autowired
    private IAttributeImageBlobService attributeImageBlobService;

    @Autowired
    private IGoodPriceService iGoodPriceService;


    @Override
    public AttributeTypeVo getAttributeVo(Long attributeId){
        AttributeTypeVo attributeTypeVo = new AttributeTypeVo();
        AttributeType attributeType = attributeTypeMapper.selectById(attributeId);
        GoodPrice price = iGoodPriceService.getById(attributeType.getPriceId());
        attributeTypeVo.setAttributeType(attributeType);
        attributeTypeVo.setGoodPrice(price);
        if (Objects.nonNull(attributeType.getImageId()) && !attributeType.getImageId().equals(0L)){
            AttributeImageBlob byId = attributeImageBlobService.getById(attributeType.getImageId());
            attributeTypeVo.setAttributeImageBlob(byId);
        }
        return attributeTypeVo;
    }

}
