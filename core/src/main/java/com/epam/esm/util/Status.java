package com.epam.esm.util;

public enum Status {
    SUCCESSFUL(200, "Successful"),
    GIFT_NOT_FOUND(1001, "Gift not found"),
    TAG_NOT_FOUND(1002, "Tag not found"),


    DEFAULT(1099, "Something wrong, try later");

    private int code;
    private String message;

    Status(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
