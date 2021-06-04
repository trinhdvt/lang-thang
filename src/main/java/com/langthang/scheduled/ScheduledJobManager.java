package com.langthang.scheduled;

import com.langthang.utils.OnLinuxCondition;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;

@Setter(onMethod_ = {@Autowired})
@EnableScheduling
@Configuration
@Conditional(OnLinuxCondition.class)
@Slf4j
public class ScheduledJobManager {

    private JobLauncher jobLauncher;

    private Job clearTrashImageJob;

    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Ho_Chi_Minh")
    public void runClearTrashImageJob() throws Exception {
        log.debug("Attempting to clear unused resource {}", new Date());
        JobParameters jobParams = new JobParametersBuilder()
                .addDate("run-time", new Date())
                .toJobParameters();
        jobLauncher.run(clearTrashImageJob, jobParams);
    }
}
