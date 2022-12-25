package com.langthang.job.crawl.rss.item;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@StepScope
@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class RssItemProcessor implements ItemProcessor<RssItemDto, RssItemModel> {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss zzz");
    private final RssItemMapper mapper;

    @Override
    public RssItemModel process(@Nullable RssItemDto item) {
        if (item == null) return null;

        item.setTitle(StringUtils.stripToEmpty(item.getTitle()));
        item.setLink(StringUtils.stripToEmpty(item.getLink()));

        var strippedDesc = Jsoup.parse(item.getDescription()).text();
        item.setDescription(strippedDesc);

        ZonedDateTime pubDate = ZonedDateTime.parse(item.getPubDate(), formatter);
        item.setPubDateInstant(pubDate.toInstant());

        return mapper.toModel(item);
    }
}
