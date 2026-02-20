package com.lablink.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final boolean success;
    private final T data;
    private final ApiError error;
    private final String timestamp;

    private ApiResponse(boolean success, T data, ApiError error) {
        this.success   = success;
        this.data      = data;
        this.error     = error;
        this.timestamp = Instant.now().toString();
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static <T> ApiResponse<T> fail(String code, String message) {
        return new ApiResponse<>(false, null, new ApiError(code, message, null));
    }

    public static <T> ApiResponse<T> fail(String code, String message, Object details) {
        return new ApiResponse<>(false, null, new ApiError(code, message, details));
    }

    public boolean isSuccess()  { return success; }
    public T getData()          { return data; }
    public ApiError getError()  { return error; }
    public String getTimestamp(){ return timestamp; }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ApiError {
        private final String code;
        private final String message;
        private final Object details;

        public ApiError(String code, String message, Object details) {
            this.code    = code;
            this.message = message;
            this.details = details;
        }

        public String getCode()    { return code; }
        public String getMessage() { return message; }
        public Object getDetails() { return details; }
    }
}
