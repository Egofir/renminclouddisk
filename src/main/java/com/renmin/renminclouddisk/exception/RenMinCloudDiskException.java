package com.renmin.renminclouddisk.exception;

public class RenMinCloudDiskException extends Exception {
    private final Integer code;
    private final String message;

    public RenMinCloudDiskException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public RenMinCloudDiskException(RenMinCloudDiskExceptionEnum exceptionEnum) {
        this(exceptionEnum.getCode(), exceptionEnum.getMessage());
    }

    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
