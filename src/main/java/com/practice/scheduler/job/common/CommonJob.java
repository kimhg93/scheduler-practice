package com.practice.scheduler.job.common;

import com.practice.scheduler.model.Schedule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 공통로직 구현을 위해 Quartz Job 인터페이스를 implement 하는 추상클래스
 * 모든 job 은 해당 추상클래스를 상속해야하며 Job 의 실행부는 doExecute 를 Override 해서 작성한다
 * @PostConstruct 를 통해 JobRegistry 에 Job 정보를 등록
 **/
@Slf4j
@RequiredArgsConstructor
public abstract class CommonJob implements Job {

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private JobRegistry jobRegistry;

    // 해당 Job 을 상속한 모든 클래스를 jobRegistry 에 등록한다
    @PostConstruct
    public void registerJob() {
        String className = this.getClass().getSimpleName();
        jobRegistry.registerJob(className, this);
    }

    @Override
    public void execute(JobExecutionContext context) {
        String executeId = UUID.randomUUID().toString();
        Schedule schedule = (Schedule) context.getJobDetail().getJobDataMap().get("schedule");

        JobDetail job = context.getJobDetail();
        String jobId = job.getKey().getName();
        beforeExecute(context, executeId);

        try {
            if(runningJobCount(jobId) == 1 || schedule.getDupYn().equalsIgnoreCase("Y")) {
                long startTime = System.currentTimeMillis();
                doExecute(context);
                long endTime = System.currentTimeMillis();
                long executionTime = endTime - startTime;
                afterExecute(context, executionTime, executeId);
            } else {
                log.info("[Job Execute Fail ] Job is Running..  {}, jobId: {}", job.getJobClass().getName(), jobId);
            }
        } catch (SchedulerException | InterruptedException e) {
            // 로깅 필요
            // notice 관련 구현
            e.printStackTrace();
        }

    }

    private int runningJobCount(String jobId) throws SchedulerException {
        int runningCount = 0;

        List<JobExecutionContext> currentlyExecutingJobs = scheduler.getCurrentlyExecutingJobs();

        for (JobExecutionContext jobContext : currentlyExecutingJobs) {
            JobDetail jobDetail = jobContext.getJobDetail();
            if(jobDetail.getKey().getName().equalsIgnoreCase(jobId)){
                runningCount++;
                if(runningCount > 1) break;
            }
        }

        return runningCount;
    }

    public void beforeExecute(JobExecutionContext context, String uuid) {
        JobDetail job = context.getJobDetail();
        String jobClassName = job.getJobClass().getName();
        String jobId = job.getKey().getName();

        // job 실행 시 db에 저장 TB_JOB_START_LOG

        String parameter;
        Object obj = context.getJobDetail().getJobDataMap().get("param");
        if(obj == null) {
            parameter = context.getJobDetail().getJobDataMap().get("schedule").toString();
        } else {
            parameter = obj.toString();
        }

        log.info("[Before job run   ][{}] {}, jobId: {}, param: {}", uuid, jobClassName, jobId, parameter);
    }

    public abstract void doExecute(JobExecutionContext context) throws InterruptedException, SchedulerException;

    public void afterExecute(JobExecutionContext context, long executionTime, String uuid) {
        JobDetail job = context.getJobDetail();
        String jobClassName = job.getJobClass().getName();
        String jobId = job.getKey().getName();
        Date nextTime = context.getTrigger().getNextFireTime();

        String next = "";
        if(nextTime != null) next = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:SSS").format(nextTime);

        // job 실행 시 db 저장 TB_JOB_END_LOG

        // notice 관련 구현
        log.info("[After job run    ][{}] {}, jobId: {}, runtime: {}ms, next => {}"
                , uuid, jobClassName, jobId, executionTime, next);
    }

}
