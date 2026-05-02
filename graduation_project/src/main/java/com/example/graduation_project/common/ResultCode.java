package com.example.graduation_project.common;

public enum ResultCode {

    SUCCESS(200, "success"),

    ERROR(500, "操作失败"),

    PARAM_ERROR(400, "参数错误"),

    UNAUTHORIZED(401, "未授权"),

    FORBIDDEN(403, "禁止访问"),

    NOT_FOUND(404, "资源不存在"),

    USER_NOT_EXIST(1001, "用户不存在"),

    USER_ALREADY_EXIST(1002, "用户已存在"),

    PASSWORD_ERROR(1003, "密码错误"),

    PRODUCT_NOT_EXIST(2001, "商品不存在"),

    PRODUCT_SOLD_OUT(2002, "商品已售罄"),

    ORDER_NOT_EXIST(3001, "订单不存在"),

    ORDER_STATUS_ERROR(3002, "订单状态错误"),
    ADDRESS_NOT_EXIST(3003, "地址不存在");

    private final Integer code;

    private final String message;

    ResultCode(Integer code, String message) {
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
