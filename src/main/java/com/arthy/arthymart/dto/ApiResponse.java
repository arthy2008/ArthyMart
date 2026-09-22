package com.arthy.arthymart.dto;

public class ApiResponse<T> {
    private boolean success;
    private String status;
    private String message;
    private T data;
    private Object error;

    public ApiResponse() {}

    public ApiResponse(boolean success, String status, String message, T data, Object error) {
        this.success = success;
        this.status = status;
        this.message = message;
        this.data = data;
        this.error = error;
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, "success", "OK", data, null);
    }

    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(true, "success", message, data, null);
    }

    public static <T> ApiResponse<T> failure(String errorMessage) {
        return new ApiResponse<>(false, "error", errorMessage, null, errorMessage);
    }

    public static <T> ApiResponse<T> failure(String status, String errorMessage) {
        return new ApiResponse<>(false, status, errorMessage, null, errorMessage);
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public Object getError() { return error; }
    public void setError(Object error) { this.error = error; }
}
