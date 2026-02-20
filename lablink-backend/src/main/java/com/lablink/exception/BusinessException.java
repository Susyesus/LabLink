package com.lablink.exception;

import org.springframework.http.HttpStatus;

public class BusinessException extends RuntimeException {

    private final String errorCode;
    private final HttpStatus httpStatus;

    public BusinessException(String errorCode, String message, HttpStatus httpStatus) {
        super(message);
        this.errorCode  = errorCode;
        this.httpStatus = httpStatus;
    }

    public String getErrorCode()    { return errorCode; }
    public HttpStatus getHttpStatus(){ return httpStatus; }

    public static BusinessException conflict(String errorCode, String message) {
        return new BusinessException(errorCode, message, HttpStatus.CONFLICT);
    }

    public static BusinessException badRequest(String errorCode, String message) {
        return new BusinessException(errorCode, message, HttpStatus.BAD_REQUEST);
    }

    public static BusinessException notFound(String errorCode, String message) {
        return new BusinessException(errorCode, message, HttpStatus.NOT_FOUND);
    }
}
