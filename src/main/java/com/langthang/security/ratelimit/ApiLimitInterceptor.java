package com.langthang.security.ratelimit;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.util.Optional;

public class ApiLimitInterceptor implements HandlerInterceptor {

    private final ApiBucketManager bucketManager;
    private final int limit;
    private final Duration duration;
    private String additionalKeyPrefix = "";

    public ApiLimitInterceptor(ApiBucketManager bucketManager, int limit, Duration duration) {
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be greater than 0");
        }
        if (duration.isNegative() || duration.isZero()) {
            throw new IllegalArgumentException("Duration must be greater than 0");
        }

        this.bucketManager = bucketManager;
        this.limit = limit;
        this.duration = duration;
    }

    public ApiLimitInterceptor(ApiBucketManager bucketManager, int limit, Duration duration, String additionalKeyPrefix) {
        this(bucketManager, limit, duration);
        this.additionalKeyPrefix = additionalKeyPrefix;
    }

    @Override
    public boolean preHandle(HttpServletRequest req, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        String bucketKey = additionalKeyPrefix;
        bucketKey += Optional.ofNullable(req.getHeader("X-Forwarded-For")).orElse(req.getRemoteAddr());

        Bucket bucket = bucketManager.resolveBucket(bucketKey, limit, duration);
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            response.addHeader("API-Rate-Limit-Remaining", probe.getRemainingTokens() + "");
            return true;
        } else {
            long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
            response.addHeader("API-Rate-Limit-Retry-After-Seconds", waitForRefill + "");
            response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(), "You have sent too much request");
            return false;
        }
    }

}