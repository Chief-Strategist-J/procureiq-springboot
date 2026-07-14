package com.procureiq.springboot_app.shared.types.single;

import com.procureiq.springboot_app.shared.types.error.ApiError;

public class ApiSingleResponse<T> {
    private String status = "success";
    private int code = 200;
    private T data;
    private ApiError error;

    public ApiSingleResponse() {}

    public ApiSingleResponse(String status, int code, T data, ApiError error) {
        this.status = status != null ? status : "success";
        this.code = code;
        this.data = data;
        this.error = error;
    }

    public static <T> ApiSingleResponse<T> success(int code, T data) {
        return new ApiSingleResponse<>("success", code, data, null);
    }

    public static <T> ApiSingleResponse<T> error(int code, String message) {
        return new ApiSingleResponse<>("error", code, null, new ApiError(message));
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ApiError getError() {
        return error;
    }

    public void setError(ApiError error) {
        this.error = error;
    }
}
