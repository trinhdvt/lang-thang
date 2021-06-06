package com.langthang.scheduled.job;

import com.langthang.model.Account;
import com.langthang.model.Notify;
import com.langthang.repository.AccountRepository;
import com.langthang.repository.NotificationRepository;
import com.langthang.services.INotificationServices;
import com.langthang.utils.constraints.NotificationType;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import java.util.Collections;

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@EnableBatchProcessing
@Configuration
public class SendFollowerNotificationJobConfig {

    private static final int BATCH_SIZE = 50;

    private final AccountRepository accountRepository;

    private final INotificationServices notificationServices;

    private final NotificationRepository notificationRepository;

    @Bean
    public Job sendFollowerNotificationJob(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        return jobBuilderFactory.get("sendFollowerNotificationJob")
                .incrementer(new RunIdIncrementer())
                .start(createBatchNotificationStep(stepBuilderFactory))
                .build();
    }

    @Bean
    public Step createBatchNotificationStep(StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("createBatchNotificationStep")
                .<Account, Notify>chunk(BATCH_SIZE)
                .reader(followerReader(null))
                .processor(notificationProcessor())
                .writer(notificationWriter())
                .build();
    }

    @Bean
    @StepScope
    public ItemReader<Account> followerReader(@Value("#{jobParameters['authorId']}") Integer accountId) {
        RepositoryItemReader<Account> reader = new RepositoryItemReader<>();
        reader.setRepository(accountRepository);
        reader.setPageSize(BATCH_SIZE);
        reader.setMethodName("getFollowedAccount");
        reader.setArguments(Collections.singletonList(accountId));
        reader.setSort(Collections.singletonMap("id", Sort.Direction.ASC));

        return reader;
    }

    @Bean
    @StepScope
    public ItemProcessor<Account, Notify> notificationProcessor() {
        return new ItemProcessor<Account, Notify>() {
            @Value("#{jobParameters['postId']}")
            private int postId;

            @Value("#{jobParameters['authorId']}")
            private int authorId;

            @Override
            public Notify process(@NonNull Account follower) {
                return notificationServices.createNotification(authorId, follower.getId(), postId, NotificationType.NEW_POST);
            }
        };
    }

    @Bean
    public ItemWriter<Notify> notificationWriter() {
        RepositoryItemWriter<Notify> writer = new RepositoryItemWriter<>();
        writer.setRepository(notificationRepository);
        return writer;
    }
}
