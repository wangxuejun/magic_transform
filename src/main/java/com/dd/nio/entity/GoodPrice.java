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
public class GoodPrice implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Double marketPrice;

    private Double channelPrice;

    private Double agreementPrice;

    private Double discount;

    private Double platformPrice;

    private Integer stock;


}
