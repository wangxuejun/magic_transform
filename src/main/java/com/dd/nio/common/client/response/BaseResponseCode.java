package com.dd.nio.common.client.response;

import lombok.Data;

@Data
public class BaseResponseCode {

    private Integer code;

    private String message;

    private Boolean success;
}
