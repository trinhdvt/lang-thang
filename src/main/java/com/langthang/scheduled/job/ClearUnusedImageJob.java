package com.langthang.scheduled.job;

import com.langthang.services.IStorageServices;
import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
@Configuration
@EnableBatchProcessing
public class ClearUnusedImageJob {
    private static final int BATCH_SIZE = 500;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private static Set<String> imageInDB = null;
    private final DataSource dataSource;
    private final IStorageServices storageServices;
    @Value("${application.image.pattern}")
    private String S3_IMAGE_URL_PATTERN;
    @Value("${cloud.aws.public.base-url}")
    private String S3_IMAGE_URL_PREFIX;
    private Pattern pattern;

    @PostConstruct
    protected void init() {
        pattern = Pattern.compile(S3_IMAGE_URL_PATTERN);
    }

    @Bean
    public Job clearTrashImageJob() {
        return new JobBuilder("clearTrashImageJob", jobRepository)
                .preventRestart()
                .listener(clearTrashImageListener())
                .start(clearTrashImageStep1())
                .next(clearTrashImageStep2())
                .build();
    }

    @Bean
    public Step clearTrashImageStep1() {
        return new StepBuilder("clearTrashImageStep1", jobRepository)
                .<String, String>chunk(BATCH_SIZE, transactionManager)
                .reader(accountTableImageReader())
                .processor(extractImageUrlProcessor())
                .writer(unusedImageFilter())
                .build();
    }

    @Bean
    public Step clearTrashImageStep2() {
        return new StepBuilder("clearTrashImageStep2", jobRepository)
                .<String, String>chunk(BATCH_SIZE, transactionManager)
                .reader(postTableImageReader())
                .processor(extractImageUrlProcessor())
                .writer(unusedImageFilter())
                .build();
    }

    @Bean
    public JobExecutionListener clearTrashImageListener() {
        return new JobExecutionListener() {
            @Override
            public void beforeJob(@NonNull JobExecution jobExecution) {
                imageInDB = Collections.synchronizedSet(new HashSet<>());
            }

            @Override
            public void afterJob(@NonNull JobExecution jobExecution) {
                log.debug("Job done!");

                Set<String> imageInS3Storage = storageServices.getAllImages();

                log.debug("File in AWS S3: {}", imageInS3Storage.size());
                log.debug("File in db: {}", imageInDB.size());

                imageInS3Storage.removeAll(imageInDB);
                log.debug("File should be remove {}", imageInS3Storage.size());
                storageServices.deleteImages(imageInS3Storage);
                imageInDB = null;
            }
        };
    }

    @Bean
    public ItemReader<String> accountTableImageReader() {
        return new JdbcCursorItemReaderBuilder<String>()
                .name("accountTableImageReader")
                .dataSource(dataSource)
                .sql("select distinct(avatar_link) from account")
                .rowMapper((rs, i) -> rs.getString(1))
                .build();
    }

    @Bean
    public ItemReader<String> postTableImageReader() {
        return new JdbcPagingItemReaderBuilder<String>()
                .name("imageReader")
                .dataSource(dataSource)
                .selectClause("select concat(post_thumbnail, '-', content) as text, id")
                .fromClause("from post")
                .sortKeys(Collections.singletonMap("id", Order.ASCENDING))
                .pageSize(BATCH_SIZE)
                .rowMapper((rs, i) -> rs.getString(1))
                .build();
    }

    public ItemProcessor<String, String> extractImageUrlProcessor() {
        return StringEscapeUtils::unescapeHtml4;
    }

    public ItemWriter<String> unusedImageFilter() {
        return items -> items.forEach(text -> {
            Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {
                String imageUrl = matcher.group(0);
                String imageName = StringUtils.replace(imageUrl, S3_IMAGE_URL_PREFIX + "/", "");
                imageInDB.add(imageName);
            }
        });
    }
}