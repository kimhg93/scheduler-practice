package com.practice.scheduler.job.api;

import java.util.Map;

/**
 * APIJob 에서 요청할 HTTP 메서드를 문자열 파라미터로 받아 해당 Enum 을 이용해 실제 요청 로직을 호출
 */
public enum HttpMethod {

    GET((url, param, handler) -> HandleHttpRequest.getInstance().get(url, param, handler)),
    POST((url, param, handler) -> HandleHttpRequest.getInstance().post(url, param, handler)),
    PUT((url, param, handler) -> HandleHttpRequest.getInstance().put(url, param, handler)),
    DELETE((url, param, handler) -> HandleHttpRequest.getInstance().delete(url, param, handler));

    private final HttpRequestHandler handler;

    HttpMethod(HttpRequestHandler handler) {
        this.handler = handler;
    }

    public void handleRequest(String url, Map<String, Object> param, APICallbackHandler apiHandler) {
        handler.handleRequest(url, param, apiHandler);
    }

    @FunctionalInterface
    public interface HttpRequestHandler {
        void handleRequest(String url, Map<String, Object> param, APICallbackHandler handler);
    }
}
