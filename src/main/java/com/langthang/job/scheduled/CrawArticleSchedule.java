package com.langthang.job.scheduled;

import com.langthang.model.constraints.Constant;
import com.langthang.utils.MyStringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;

@EnableScheduling
@Configuration
@Slf4j
@Profile("production")
public class CrawArticleSchedule {

    private final JobLauncher jobLauncher;
    private final JobRegistry jobRegistry;

    @Autowired
    public CrawArticleSchedule(JobLauncher jobLauncher, JobRegistry jobRegistry) {
        this.jobLauncher = jobLauncher;
        this.jobRegistry = jobRegistry;
    }

    @Scheduled(cron = "0 0 21 * * *")
    public void startCrawRssItem() throws NoSuchJobException, JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        var jobName = Constant.READ_RSS_ITEM_JOB;
        JobParameters jobParams = new JobParametersBuilder()
                .addString(
                        "resource-folder",
                        String.format("rss/%s", MyStringUtils.getTodayString())
                ).addDate("run-time", new Date())
                .toJobParameters();

        var job = jobRegistry.getJob(jobName);
        jobLauncher.run(job, jobParams);
    }

    @Scheduled(cron = "0 0 1 * * *")
    public void startCrawlArticle() throws NoSuchJobException, JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        var jobName = Constant.READ_ARTICLE_JOB;
        JobParameters jobParams = new JobParametersBuilder()
                .addDate("run-time", new Date())
                .toJobParameters();

        var job = jobRegistry.getJob(jobName);
        jobLauncher.run(job, jobParams);
    }
}
