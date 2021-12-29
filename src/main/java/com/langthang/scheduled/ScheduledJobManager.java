package com.langthang.scheduled;

import com.langthang.scheduled.job.BackupDatabaseJob;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

@Setter(onMethod_ = {@Autowired})
@EnableScheduling
@Component
@Slf4j
@ConditionalOnProperty(prefix = "spring", name = "env", havingValue = "prod")
public class ScheduledJobManager {

    private JobLauncher jobLauncher;

    private Job clearTrashImageJob;

    private BackupDatabaseJob backupDatabaseJob;

    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Ho_Chi_Minh")
    public void runClearTrashImageJob() throws Exception {
        log.debug("Attempting to clear unused resource {}", new Date());
        JobParameters jobParams = new JobParametersBuilder()
                .addDate("run-time", new Date())
                .toJobParameters();
        jobLauncher.run(clearTrashImageJob, jobParams);
    }

    @Scheduled(cron = "0 30 0 * * ?", zone = "Asia/Ho_Chi_Minh")
    public void runBackupDBJob() throws IOException {
        log.debug("Attempting to backup database {}", new Date());
        backupDatabaseJob.run();
    }

}