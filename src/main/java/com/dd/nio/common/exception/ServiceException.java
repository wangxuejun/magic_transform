package com.dd.nio.common.exception;


/**
 * service exception
 */
public class ServiceException extends RuntimeException {

    /**
     * code
     */
    private Integer code;

    public ServiceException() {
    }

    public ServiceException(Integer code,String message) {
        super(message);
        this.code = code;
    }

    public ServiceException(String message) {
        super(message);
        this.code = 400;
    }

    public Integer getCode() {
        return this.code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}