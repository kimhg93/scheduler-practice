package com.practice.scheduler.exception;

import org.springframework.http.HttpStatus;

public class ScheduleException extends RuntimeException {
    private String msg;
    private HttpStatus status;

    public ScheduleException(String msg, HttpStatus status){
        this.msg = msg;
        this.status = status;
    }

    public ScheduleException() {}

    public ScheduleException(String msg){
        this.msg = msg;
    }

    public ScheduleException(HttpStatus status){
        this.status = status;
    }

    public ScheduleException(Exception e){
        this.msg = e.getMessage();
    }

    public String getMsg() {
        return msg;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
