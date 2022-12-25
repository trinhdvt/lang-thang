package com.langthang.job.crawl.rss.feed;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Instant;

@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
public class RssFeedProcessTasklet implements Tasklet, StepExecutionListener {

    private final RssFeedRepository rssFeedRepository;

    private String resourceFolder = "";

    @Override
    public RepeatStatus execute(@NonNull StepContribution contribution, @NonNull ChunkContext chunkContext) throws Exception {
        var jobParams = chunkContext.getStepContext().getStepExecution().getJobParameters();
        this.resourceFolder = jobParams.getString("resource-folder");

        var rssSource = rssFeedRepository.findAll();
        for (RssFeedModel rss : rssSource) {
            log.debug("Processing RssFeedModel: {}", rss);

            downloadRss(rss.getSource());

            rss.setLastProcess(Instant.now());
        }

        rssFeedRepository.saveAll(rssSource);
        return RepeatStatus.FINISHED;
    }

    @Override
    public void beforeStep(@NonNull StepExecution stepExecution) {
        log.info("Start reading from rss source");
    }

    @Override
    public ExitStatus afterStep(@NonNull StepExecution stepExecution) {
        log.info("Finish reading from rss source");
        return ExitStatus.COMPLETED;
    }

    private void downloadRss(String sourceUrl) throws IOException {
        var dir = resourceFolder;
        if (!Files.exists(new File(dir).toPath())) {
            Files.createDirectories(new File(dir).toPath());
        }

        var fileName = Instant.now().toEpochMilli() + ".xml";
        try (var in = new UrlResource(sourceUrl).getInputStream()) {
            File outFile = new File(dir + "/" + fileName);
            Files.copy(in, outFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            var downloadedFile = outFile.getAbsolutePath();
            log.debug("File is downloaded to {}", downloadedFile);
        }

    }
}
