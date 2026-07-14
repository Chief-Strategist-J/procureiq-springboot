package com.procureiq.springboot_app.shared.types.error;

public class ApiError {
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
