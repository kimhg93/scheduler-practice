package com.practice.scheduler.controller;

import com.practice.scheduler.job.common.JobRegistry;
import com.practice.scheduler.model.RequestDto;
import com.practice.scheduler.model.Schedule;
import com.practice.scheduler.service.SchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RestController
@RequestMapping(value = "/api")
@RequiredArgsConstructor
public class SchedulerController {

    private final SchedulerService schedulerService;
    private final JobRegistry jobRegistry;
    private final Scheduler scheduler;

    /** todo
     *   cron 생성 api (초 분 시 일 월 년) (반복, 1회)
     *   스케줄러가 실행중일때 업데이트 어떻게할거
     */

    /**
     * 스케줄러 실행
     * @return
     */
    @GetMapping(value = "/init")
    public ResponseEntity<?> runScheduler() throws SchedulerException {
        schedulerService.addAllSchedule();
        return ResponseEntity.noContent().build();
    }

    /**
     * 전체 스케줄 reload (실행중인 job 대기 후 reload)
     * @return
     * @throws SchedulerException
     */
    @GetMapping(value = "/reload")
    public ResponseEntity<?> startScheduler() throws SchedulerException {
        if(schedulerService.clearAllSchedule()){
            schedulerService.addAllSchedule();
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * 전체 스케줄 목록 조회
     * @return
     * @throws SchedulerException
     */
    @GetMapping(value = "/schedule")
    public ResponseEntity<List<Schedule>> selectAllSchedule() {
        return ResponseEntity.ok().body(schedulerService.selectAllSchedule());
    }

    /**
     * 스케줄 조회
     * @param id 스케줄 id
     * @return
     */
    @GetMapping(value = "/schedule/{id}")
    public ResponseEntity<Schedule> selectSchedule(@PathVariable Long id) {
        return ResponseEntity.ok().body(schedulerService.selectSchedule(id));
    }

    /**
     * 스케줄 저장
     * @param dto
     * @return
     */
    @PostMapping(value = "/schedule")
    public ResponseEntity<Schedule> createSchedule(@RequestBody RequestDto dto) throws SchedulerException{
        Schedule result = schedulerService.createSchedule(dto);
        // 스케줄러가 실행중이면 신규 등록 job 을 바로 스케줄러에 등록함
        if(scheduler.isStarted()){
            schedulerService.addSchedule(result);
        }
        WebMvcLinkBuilder self = linkTo(methodOn(SchedulerController.class).selectSchedule(result.getId()));
        return ResponseEntity.created(self.toUri()).body(result);
    }

    /**
     * 스케줄 수정
     * @param dto
     * @return
     */
    @PutMapping(value = "/schedule")
    public ResponseEntity<Schedule> updateSchedule(@RequestBody RequestDto dto) {
        return ResponseEntity.ok().body(schedulerService.updateSchedule(dto));
    }

    /**
     * 스케줄 삭제
     * @param id 스케줄 id
     */
    @DeleteMapping(value = "/schedule/{id}")
    public void deleteSchedule(@PathVariable Long id) {
        schedulerService.deleteSchedule(id);
    }

    /**
     * 실행중인 스케줄 확인
     * @return
     * @throws SchedulerException
     */
    @GetMapping(value = "/schedule/running")
    public ResponseEntity<List<Map<String, Object>>> getRunning() throws SchedulerException {
        return ResponseEntity.ok().body(schedulerService.getRunningJobs());
    }

    /**
     * JobRegistry에 등록된 모든 job을 조회
     * @return
     */
    @GetMapping(value = "/job/registry")
    public ResponseEntity<List<Map<String, Object>>> getAllJobs() {
        return ResponseEntity.ok().body(jobRegistry.getAllJobInfo());
    }

    /**
     * 특정 job을 즉시 실행한다.
     * @param dto (classNm, paramString)
     * @throws SchedulerException
     */
    @PostMapping(value = "/job/trigger")
    public ResponseEntity<?> triggerJobNow(@RequestBody RequestDto dto) throws SchedulerException {
        schedulerService.triggerJobNow(dto);
        return ResponseEntity.noContent().build();
    }

}
