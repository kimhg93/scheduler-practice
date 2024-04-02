package com.practice.scheduler.job.api;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 리플렉션을 사용하지 않기 위해 Bean을 관리하는 Registry를 구현
 * APICallback 클래스명과 클래스를 Map에 담아 관리
 * APICallbackHandler를 상속한 Bean 이 초기화 될때 @PostConstruct 를 통해 이 APICallbackRegistry 에 자동으로 정보를 등록함
 */
@Component
public class APICallbackRegistry {

    private Map<String, APICallbackHandler> handlers = new ConcurrentHashMap<>();

    public void registerHandler(String handlerName, APICallbackHandler handler) {
        handlers.put(handlerName, handler);
    }

    public APICallbackHandler getHandler(String handlerName) {
        return handlers.get(handlerName);
    }

    public Map<String, APICallbackHandler> getAllHandlers() {
        return handlers;
    }

}
