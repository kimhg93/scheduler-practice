package com.practice.scheduler.job.specific;

import com.practice.scheduler.job.common.CommonJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

import java.util.Date;


@Slf4j
@Component
public class HelloJob extends CommonJob {

    @Override
    public void doExecute(JobExecutionContext context) throws InterruptedException {
        Thread.sleep(50000);

        String str = context.getJobDetail().getJobDataMap().get("param").toString();

        log.info(str);
        log.info("Hello World! - " + new Date());
    }

}