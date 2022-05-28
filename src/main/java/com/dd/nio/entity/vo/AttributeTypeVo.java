package com.dd.nio.entity.vo;

import com.dd.nio.entity.AttributeImageBlob;
import com.dd.nio.entity.AttributeType;
import com.dd.nio.entity.GoodPrice;
import lombok.Data;

@Data
public class AttributeTypeVo {

    private AttributeType attributeType;

    private AttributeImageBlob attributeImageBlob;

    private GoodPrice goodPrice;
}
