package com.practice.scheduler.service;

import com.practice.scheduler.exception.ScheduleException;
import com.practice.scheduler.job.api.APICallbackRegistry;
import com.practice.scheduler.job.api.APIJob;
import com.practice.scheduler.job.common.JobRegistry;
import com.practice.scheduler.model.RequestDto;
import com.practice.scheduler.model.Schedule;
import com.practice.scheduler.repository.SchedulerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerService implements ApplicationListener<ApplicationReadyEvent> {

    // 추후 CommonJob에서 사용될 공통 로직 구현
    // 스케줄 정보 DB 조회, 로그 DB 적재, 결과 DB 적재 등

    private final JobRegistry jobRegistry;
    private final SchedulerRepository schedulerRepository;
    private final APICallbackRegistry apiCallbackRegistry;

    SchedulerFactory sf = new StdSchedulerFactory();

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        //run();
    }

    /**
     * 스케줄러에 job을 등록하고 스케줄러를 실행
     */
    public void run() {
        try {
            addAllSchedule();
            startScheduler();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * job과 스케줄 정보를 조회하여 스케줄러에 등록한다.
     * @throws SchedulerException
     */
    public void addAllSchedule() throws SchedulerException {
        for(Schedule vo : selectAllSchedule()){
            addSchedule(vo);
        }
    }

    public JobDetail initJob(Schedule vo) {
        Map<String, Job> jobs = jobRegistry.getAllJobs();

        JobDetail job;
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("schedule", vo);

        if(vo.getApiYn().equalsIgnoreCase("Y")){
            jobDataMap.put("apiCallbackRegistry", apiCallbackRegistry);
            job = newJob(jobs.get("APIJob").getClass())
                    .withIdentity(vo.getJobId(), "group1")
                    .usingJobData(jobDataMap)
                    .build();
        } else {
            jobDataMap.put("param", vo.getParameter());
            job = newJob(jobs.get(vo.getClassNm()).getClass())
                    .withIdentity(vo.getJobId(), "group1")
                    .usingJobData(jobDataMap)
                    .build();
        }

        return job;
    }

    public void addSchedule(Schedule vo) throws SchedulerException {
        Scheduler scheduler = sf.getScheduler();
        JobDetail job = initJob(vo);

        CronTrigger trigger = newTrigger()
                .withIdentity(vo.getJobId(), "group1")
                .withSchedule(CronScheduleBuilder.cronSchedule(vo.getCron()))
                .build();

        scheduler.scheduleJob(job, trigger);
    }

    public void updateSchedule(Schedule vo) throws SchedulerException {
        Scheduler scheduler = sf.getScheduler();

        TriggerKey triggerKey = new TriggerKey(vo.getJobId(), "group1");
        CronTrigger trigger = newTrigger()
                .withIdentity(vo.getJobId(), "group1")
                .withSchedule(CronScheduleBuilder.cronSchedule(vo.getCron()))
                .build();

        scheduler.rescheduleJob(triggerKey, trigger);
    }

    /**
     * 스케줄러를 실행한다.
     * @throws SchedulerException
     */
    public void startScheduler() throws SchedulerException {
        log.info("------- Initializing ----------------------");
        Scheduler scheduler = sf.getScheduler();
        log.info("------- Initialization Complete -----------");

        log.info("------- Scheduling Job  -------------------");
        scheduler.start();
        log.info("------- Started Scheduler -----------------");
    }

    /**
     * 스케줄러를 종료 한다.
     * scheduler.shutdown(args) args 가 true 일 경우 실행중인 작업 대기 후 종료 false 일 경우 바로 종료
     * @throws SchedulerException
     */
    public boolean shutdown(boolean waitForJobsToComplete) throws SchedulerException {
        Scheduler scheduler = sf.getScheduler();

        log.info("------- Shutting Down ---------------------");
        scheduler.shutdown(waitForJobsToComplete);
        log.info("------- Shutdown Complete -----------------");

        while(!scheduler.isShutdown()){
        }

        return scheduler.isShutdown();
    }


    /**
     * 현재 실행중인 (전체)job에 대한 정보를 가져온다
     * @return
     * @throws SchedulerException
     */
    public List<Map<String, Object>> getRunningJobs() throws SchedulerException {
        Scheduler scheduler = sf.getScheduler();

        List<Map<String, Object>> list = new ArrayList<>();

        List<JobExecutionContext> currentlyExecutingJobs = scheduler.getCurrentlyExecutingJobs();
        for (JobExecutionContext jobContext : currentlyExecutingJobs) {
            JobDetail jobDetail = jobContext.getJobDetail();

            Map<String, Object> map = new HashMap<>();
            map.put(jobDetail.getKey().getName(), jobDetail.getKey().getGroup());
            list.add(map);
        }

        return list;
    }

    public Schedule createSchedule(RequestDto dto) {
        if(!schedulerRepository.findByJobId(dto.getJobId()).isPresent()){
            return schedulerRepository.save(Schedule.builder()
                    .classNm(dto.getClassNm())
                    .jobId(dto.getJobId())
                    .cron(dto.getCron())
                    .paramString(dto.getParamString())
                    .dupYn(dto.getDupYn())
                    .apiUrl(dto.getApiUrl())
                    .method(dto.getMethod())
                    .apiYn(dto.getApiYn())
                    .build());
        } else throw new ScheduleException("중복된 JobId 가 존재합니다.", HttpStatus.BAD_REQUEST);
    }

    @Transactional
    public Schedule updateSchedule(RequestDto dto) {
        Schedule schedule = schedulerRepository.findById(dto.getId())
                .orElseThrow(
                        () -> new ScheduleException("데이터가 존재하지 않습니다.", HttpStatus.BAD_REQUEST));
        schedule.updateSchedule(dto);
        return schedule;
    }

    public void deleteSchedule(Long id) {
        schedulerRepository.deleteById(id);
    }

    public List<Schedule> selectAllSchedule() {
        return schedulerRepository.findAll();
    }

    public Schedule selectSchedule(Long id) {
        return schedulerRepository.findById(id).orElseGet(Schedule::new);
    }

    /**
     * 특정 job을 즉시 실행한다.
     * @param dto
     * @throws SchedulerException
     */
    public void triggerJobNow (RequestDto dto) throws SchedulerException {

        Map<String, Job> jobs = jobRegistry.getAllJobs();
        JobDataMap jobDataMap = new JobDataMap();

        System.err.println(dto.getParameter());

        jobDataMap.put("param", dto.getParameter());

        JobDetail jobDetail = newJob(jobs.get(dto.getClassNm()).getClass())
                .withIdentity(dto.getClassNm()+"-TriggerNowJob", "group1")
                .usingJobData(jobDataMap)
                .build();

        Trigger trigger = newTrigger()
                .withIdentity(dto.getClassNm() + "-TriggerNow", "group1")
                .startNow()
                .withSchedule(simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();

        sf.getScheduler().scheduleJob(jobDetail, trigger);
    }

}
