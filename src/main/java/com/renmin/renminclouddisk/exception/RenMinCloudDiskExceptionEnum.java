package com.renmin.renminclouddisk.exception;

public enum RenMinCloudDiskExceptionEnum {
    NEED_USER_NAME(10001, "The user name cannot be empty"),
    NEED_PASSWORD(10002, "The password cannot be empty"),
    SENSITIVEWORD_EXISTED(10003, "Username contains sensitive words"),
    PASSWORD_NOT_CONFORM(10004, "The password is invalid"),
    NAME_EXISTED(10005, "Registration failed because the same name is not allowed");
    private Integer code;
    private String message;

    RenMinCloudDiskExceptionEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
