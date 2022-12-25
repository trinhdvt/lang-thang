package com.langthang.job.crawl.article;

import com.langthang.job.ChunkProcessTimeLogger;
import com.langthang.job.crawl.parser.DuLichVietNameParser;
import com.langthang.job.crawl.rss.item.RssItemModel;
import com.langthang.job.crawl.rss.item.RssItemModel_;
import com.langthang.job.crawl.rss.item.RssItemRepository;
import com.langthang.model.constraints.Constant;
import com.langthang.model.entity.Post;
import com.langthang.repository.AccountRepository;
import com.langthang.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Set;

@Configuration
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class ArticleCrawlerJob {
    private final AccountRepository accountRepository;
    private final PostRepository postRepository;
    private final RssItemRepository rssItemRepository;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;


    @Bean
    public Job readArticleJob() {
        return new JobBuilder(Constant.READ_ARTICLE_JOB, jobRepository)
                .start(crawlArticleStep())
                .build();
    }


    @Bean
    public Step crawlArticleStep() {
        return new StepBuilder(Constant.READ_ARTICLE_STEP, jobRepository)
                .<RssItemModel, Post>chunk(5, transactionManager)
                .reader(articleReader())
                .processor(articleProcessor())
                .writer(articleWriter())
                .listener(new ChunkProcessTimeLogger())
                .build();
    }

    @Bean
    @StepScope
    public ItemReader<RssItemModel> articleReader() {
        return new JdbcPagingItemReaderBuilder<RssItemModel>()
                .name("articleReader")
                .selectClause("SELECT *")
                .fromClause("FROM rss_item")
                .whereClause("WHERE is_processed = false")
                .rowMapper(new BeanPropertyRowMapper<>(RssItemModel.class))
                .fetchSize(5)
                .sortKeys(Map.of(RssItemModel_.ID, Order.ASCENDING))
                .dataSource(dataSource)
                .saveState(false)
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<RssItemModel, Post> articleProcessor() {
        var author = accountRepository.findById(1).orElseThrow(() -> new RuntimeException("Author not found"));
        return item -> {
            Post post = new DuLichVietNameParser(item).parse();
            post.setPublished(true);
            post.setAuthor(author);

            rssItemRepository.updateStatus(Set.of(item.getId()), true);
            return post;
        };
    }

    @Bean
    @StepScope
    public ItemWriter<Post> articleWriter() {
        var itemWriter = new RepositoryItemWriter<Post>();
        itemWriter.setRepository(postRepository);
        return itemWriter;
    }
}
