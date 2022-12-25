package com.langthang.job.crawl.rss.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@StepScope
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
public class RssItemWriter implements ItemWriter<RssItemModel> {

    private final RssItemRepository rssItemRepository;

    @Override
    public void write(@NonNull Chunk<? extends RssItemModel> chunk) {
        rssItemRepository.saveAll((List<RssItemModel>) chunk.getItems());
    }
}
