package com.procureiq.springboot_app.shared.types;

public class ApiResponse<T> {
    private String status = "success";
    private int code = 200;
    private T data;
    private ApiError error;

    public ApiResponse() {}

    public ApiResponse(String status, int code, T data, ApiError error) {
        this.status = status != null ? status : "success";
        this.code = code;
        this.data = data;
        this.error = error;
    }

    public static <T> ApiResponse<T> success(int code, T data) {
        return new ApiResponse<>("success", code, data, null);
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>("error", code, null, new ApiError(message));
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

    public static class ApiError {
        private String message = "";

        public ApiError() {}

        public ApiError(String message) {
            this.message = message != null ? message : "";
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
