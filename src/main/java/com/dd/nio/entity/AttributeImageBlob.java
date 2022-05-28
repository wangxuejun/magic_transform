package com.dd.nio.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.sql.Blob;
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
public class AttributeImageBlob implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String imageUrl;

    private Blob imageBlob;


}
