package com.hitstudio.expensetracker.util.concurrent;

public class Result<T> {
    public final T data;
    public final String message;
    public final boolean success;

    private Result(T data, String message, boolean success) {
        this.data = data;
        this.message = message;
        this.success = success;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(data, null, true);
    }

    public static <T> Result<T> error(String message) {
        return new Result<>(null, message, false);
    }
}
