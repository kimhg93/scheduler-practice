package com.practice.scheduler.job.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class TestCallback extends APICallbackHandler {

    @Override
    public void onSuccess(Map<String, Object> map) {
       log.info(map.toString());
    }

    @Override
    public void onError(Throwable error) {
        log.error("ERROR !!!!!!!!!!!!!!!!!!!");
        log.error(error.toString());
    }


}
