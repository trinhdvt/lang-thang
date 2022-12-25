package com.langthang.job.crawl.rss.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;

@Component
@Slf4j
public class RssItemListener implements StepExecutionListener {

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        var resourceFolder = stepExecution.getJobParameters().getString("resource-folder");
        log.info("Deleting all xml file in folder: {}", resourceFolder);

        try {
            ResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
            var pattern = String.format("file:%s/*.xml", resourceFolder);
            Resource[] resources = patternResolver.getResources(pattern);

            for (var resource : resources) {
                var file = resource.getFile();
                Files.deleteIfExists(file.toPath());
            }

            return ExitStatus.COMPLETED;

        } catch (IOException e) {
            log.error("Fail to delete resource file {}", e.getMessage());
            return ExitStatus.FAILED;
        }
    }
}
