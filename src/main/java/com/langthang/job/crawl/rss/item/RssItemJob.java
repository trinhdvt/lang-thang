package com.langthang.job.crawl.rss.item;

import com.langthang.job.crawl.rss.feed.RssFeedProcessTasklet;
import com.langthang.model.constraints.Constant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
@Configuration
public class RssItemJob {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final TaskExecutor taskExecutor;

    private final ItemReader<RssItemDto> rssItemReader;
    private final RssItemWriter rssItemItemWriter;
    private final RssItemProcessor rssItemProcessor;
    private final RssItemListener rssItemListener;

    private final RssFeedProcessTasklet rssFeedProcessTasklet;

    @Bean
    public Job readRssItemJob() {
        return new JobBuilder(Constant.READ_RSS_ITEM_JOB, jobRepository)
                .preventRestart()
                .incrementer(new RunIdIncrementer())
                .start(readRssSourceStep())
                .next(readRssItemStep())
                .build();
    }

    @Bean
    public Step readRssSourceStep() {
        return new StepBuilder(Constant.READ_RSS_SOURCE_STEP, jobRepository)
                .tasklet(rssFeedProcessTasklet, transactionManager)
                .build();
    }

    @Bean
    public Step readRssItemStep() {
        return new StepBuilder(Constant.READ_RSS_ITEM_STEP, jobRepository)
                .<RssItemDto, RssItemModel>chunk(5, transactionManager)
                .reader(rssItemReader)
                .processor(rssItemProcessor)
                .writer(rssItemItemWriter)
                .listener(rssItemListener)
                .taskExecutor(taskExecutor)
                .build();
    }

}

