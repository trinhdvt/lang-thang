package com.langthang.job.crawl.rss.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RssItemMapper {
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "pubDate", source = "pubDateInstant")
    RssItemModel toModel(RssItemDto rssItemDto);

}
