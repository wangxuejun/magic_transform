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
 * @since 2022-05-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Good implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 商品标题
     */
    private String name;

    /**
     * 类型id
     */
    private String categoryString;

    private Integer isEffect;


}
