package com.practice.scheduler.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ScheduleException.class)
    public ResponseEntity<Object> handleCustomException(ScheduleException e) {
        String msg = e.getMsg();
        HttpStatus status = e.getStatus();

        Map<String, Object> response = new HashMap<>();
        response.put("message", msg);
        response.put("status", status);
        response.put("error", status.name());
        response.put("timestamp", LocalDateTime.now());
        return new ResponseEntity<>(response, status);
    }

}
