package com.dd.nio.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.sql.Timestamp;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * <p>
 * 
 * </p>
 *
 * @author tao.wang15
 * @since 2022-05-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @NotBlank(message = "userName is null")
    private String userName;

    @NotBlank(message = "password is null")
    private String password;

    @NotNull(message = "exprTime is null")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date exprTime;

    @NotBlank(message = "userRole is null")
    private String userRole;

    @NotBlank(message = "shopUsername is null")
    private String shopUsername;

    @NotBlank(message = "shopPassword is null")
    private String shopPassword;


}
