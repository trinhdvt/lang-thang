package com.langthang.job.crawl.rss.item;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.*;

import java.time.Instant;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "item")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RssItemDto {
    private String title;
    private String description;
    private String link;
    private String pubDate;
    private Instant pubDateInstant;
}
