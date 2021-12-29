package com.langthang.security.ratelimit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class ApiBucketManager {

    @Cacheable(cacheNames = "api-rate-limit-cache", key = "#key", sync = true)
    public Bucket resolveBucket(String key, int limit, Duration duration) {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(limit, Refill.intervally(limit, duration)))
                .build();
    }

}