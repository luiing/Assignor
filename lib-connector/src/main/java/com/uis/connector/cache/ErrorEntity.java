package com.uis.connector.cache;

/**
 * 错误信息回调
 * @author uis
 */
public class ErrorEntity {
    public String error;
    public int errorCode;

    public ErrorEntity(String message) {
        this(0,message);
    }

    public ErrorEntity(int code,String message) {
        this.errorCode = code;
        this.error = message;
    }
}
