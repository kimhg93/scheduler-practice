package com.practice.scheduler.job.common;

import org.quartz.Job;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 리플렉션을 사용하지 않기 위해 Bean을 관리하는 Registry를 구현
 * Job 클래스명과 클래스를 Map에 담아 관리
 * CommonJob 을 상속한 Job Bean 이 초기화 될때 @PostConstruct 를 통해 이 JobRegistry 에 자동으로 정보를 등록함
 */
@Component
public class JobRegistry {

    private Map<String, Job> tasks = new ConcurrentHashMap<>();

    public void registerJob(String className, Job task) {
        tasks.put(className, task);
    }

    public Job getJob(String jobName) {
        return tasks.get(jobName);
    }

    public Map<String, Job> getAllJobs() {
        return tasks;
    }

    public List<Map<String, Object>> getAllJobInfo() {
        List<Map<String, Object>> result = new ArrayList<>();
        tasks.forEach((key, value) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("className", key);
            map.put("classFullPath", value.getClass().getName());
            result.add(map);
        });

        return result;
    }

}
