package com.practice.scheduler.job.api;

import com.practice.scheduler.job.common.CommonJob;
import com.practice.scheduler.model.Schedule;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 외, 내부 REST API 를 호출하여 처리하는 스케줄이 필요할 경우 사용하는 job
 * REST API 호출은 해당 Job을 공통적으로 사용하며 APICallbackHandler 를 상속하여 각 요청별 콜백과 에러처리만 구현
 */
@Component
public class APIJob extends CommonJob {

    @Override
    public void doExecute(JobExecutionContext context) {
        APICallbackRegistry registry = (APICallbackRegistry) context.getJobDetail()
                .getJobDataMap()
                .get("apiCallbackRegistry");

        Schedule schedule = (Schedule) context.getJobDetail()
                .getJobDataMap()
                .get("schedule");

        String url = schedule.getApiUrl();
        String method = schedule.getMethod();
        String callback = schedule.getCallback();
        Map<String, Object> param = (Map<String, Object>) schedule.getParameter();

        APICallbackHandler handler = registry.getHandler(callback);

        HttpMethod.valueOf(method.toUpperCase()).handleRequest(url, param, handler);
    }

}
