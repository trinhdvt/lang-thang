package com.langthang.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
public class ApiBucketManager {

    @Cacheable(cacheNames = "api-rate-limit-cache", key = "#key", sync = true)
    public Bucket resolveBucket(String key, int limit, Duration duration) {
        log.debug("Created new Bucket with key {}, limit {}, in {} s", key, limit, duration.toMillis() / 1000);

        return Bucket4j.builder()
                .addLimit(Bandwidth.classic(limit, Refill.intervally(limit, duration)))
                .build();
    }

}
