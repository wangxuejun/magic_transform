package com.dd.nio.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author tao.wang15
 * @since 2022-05-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class GoodAttribute implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long goodId;

    /**
     * 属性值
     */
    private String attributeValue;

    /**
     * 属性key
     */
    private String attributeKey;

    /**
     * 分组
     */
    private String groupKey;

    /**
     * 分组id
     */
    private Long propertyId;

    /**
     * 其他字段
     */
    private String other;


}
