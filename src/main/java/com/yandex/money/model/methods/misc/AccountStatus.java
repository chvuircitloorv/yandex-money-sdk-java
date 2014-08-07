package com.yandex.money.model.methods.misc;

public enum AccountStatus {
    ANONYMOUS("anonymous"),
    NAMED("named"),
    IDENTIFIED("identified"),
    UNKNOWN("unknown");

    private final String code;

    AccountStatus(String code) {
        this.code = code;
    }

    public static AccountStatus parse(String code) {
        if (code == null) {
            return null;
        }
        for (AccountStatus value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return UNKNOWN;
    }

    public String getCode() {
        return code;
    }
}