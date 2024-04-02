package com.practice.scheduler.job.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * APIJob 을 통해 REST API 를 호출한 이후 콜백, 에러처리를 위해 해당 추상클래스를 상속해 구현해야 한다.
 */
public abstract class APICallbackHandler {

    @Autowired
    private ApplicationContext context;

    @PostConstruct
    public void registerJob() {
        APICallbackRegistry registry = context.getBean(APICallbackRegistry.class);
        String className = this.getClass().getSimpleName();
        registry.registerHandler(className, this);
    }

    // response 를 받아서 처리하는 메서드
    public abstract void onSuccess(Map<String, Object> map);

    // 에러가 발생했을 경우 처리하는 메서드
    public abstract void onError(Throwable error);

}
