package com.dd.nio.service;

import com.dd.nio.entity.AttributeType;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dd.nio.entity.vo.AttributeTypeVo;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author tao.wang15
 * @since 2022-05-27
 */
@Service
public interface IAttributeTypeService extends IService<AttributeType> {

    AttributeTypeVo getAttributeVo(Long attributeId);
}
