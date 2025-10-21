package com.chuncongcong.framework.exception;

public class ServiceException extends RuntimeException {

    private final int code;

    public ServiceException(int code, String message) {
        super(message);
        this.code = code;
    }

    public ServiceException(String message) {
        super(message);
        this.code = 500;
    }

    public ServiceException(Exception e) {
        super(e.getMessage());
        this.code = 500;
    }

    public static ServiceException instance(ServiceErrorEnum errorEnum) {
        return new ServiceException(errorEnum.getCode(), errorEnum.getDesc());
    }

    public static ServiceException instance(int errorCode, String errMsg) {
        return new ServiceException(errorCode, errMsg);
    }

    public int getCode() {
        return code;
    }
}
