package com.epam.esm.util;

public enum Status {
    SUCCESSFUL(200, "Successful"),
    GIFT_NOT_FOUND(1001, "Gift not found"),
    TAG_NOT_FOUND(1002, "Tag not found"),


    DEFAULT(1099, "Something wrong, try later");

    private Integer code;
    private String message;

    Status(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
