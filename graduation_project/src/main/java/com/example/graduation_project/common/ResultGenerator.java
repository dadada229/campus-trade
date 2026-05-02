package com.example.graduation_project.common;

import java.util.Collections;

public class ResultGenerator {

    public static <T> Result<T> success() {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), (T) Collections.emptyMap());
    }

    public static <T> Result<T> success(T data) {
        T safeData = data != null ? data : (T) Collections.emptyMap();
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), safeData);
    }

    public static <T> Result<T> success(String message, T data) {
        T safeData = data != null ? data : (T) Collections.emptyMap();
        return new Result<>(ResultCode.SUCCESS.getCode(), message, safeData);
    }

    public static <T> Result<T> error() {
        return new Result<>(ResultCode.ERROR.getCode(), ResultCode.ERROR.getMessage(), (T) Collections.emptyMap());
    }

    public static <T> Result<T> error(String message) {
        return new Result<>(ResultCode.ERROR.getCode(), message, (T) Collections.emptyMap());
    }

    public static <T> Result<T> error(ResultCode resultCode) {
        return new Result<>(resultCode.getCode(), resultCode.getMessage(), (T) Collections.emptyMap());
    }

    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, (T) Collections.emptyMap());
    }

}
