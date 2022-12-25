package com.langthang.job.crawl.rss.feed;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Table(name = "rss_feed")
@Entity
@Data
public class RssFeedModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String source;
    private Instant lastProcess;
}
