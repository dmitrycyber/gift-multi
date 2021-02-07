package com.epam.esm.util;

public enum Status {
    GIFT_NOT_FOUND(40401),
    TAG_NOT_FOUND(40402),
    VALIDATION_EXCEPTION(40002),
    TAG_NAME_ALREADY_REGISTERED(40003),
    DEFAULT(1099);

    private Integer code;

    Status(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
