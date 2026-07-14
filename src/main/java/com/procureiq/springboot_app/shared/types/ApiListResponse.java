package com.procureiq.springboot_app.shared.types;

import java.util.List;

public class ApiListResponse<T> {
    private String status = "success";
    private int code = 200;
    private List<T> data;
    private ApiError error;
    private int totalCount;

    public ApiListResponse() {}

    public ApiListResponse(String status, int code, List<T> data, ApiError error, int totalCount) {
        this.status = status != null ? status : "success";
        this.code = code;
        this.data = data;
        this.error = error;
        this.totalCount = totalCount;
    }

    public static <T> ApiListResponse<T> success(int code, List<T> data) {
        int count = data != null ? data.size() : 0;
        return new ApiListResponse<>("success", code, data, null, count);
    }

    public static <T> ApiListResponse<T> error(int code, String message) {
        return new ApiListResponse<>("error", code, null, new ApiError(message), 0);
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

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public ApiError getError() {
        return error;
    }

    public void setError(ApiError error) {
        this.error = error;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
