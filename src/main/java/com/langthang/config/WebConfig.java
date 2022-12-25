package com.langthang.config;

import com.langthang.security.ratelimit.ApiBucketManager;
import com.langthang.security.ratelimit.ApiLimitInterceptor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
public class WebConfig implements WebMvcConfigurer {

    private final ApiBucketManager apiBucketManager;
    @Value("${spring.api-limit.enabled}")
    private boolean enabledApiLimit;

    @Value("${security.cors.allowed-origins}")
    private String[] allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD")
                .allowCredentials(true);
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        log.warn("Prefixing all API in controller.v2 package with /v2");

        configurer.addPathPrefix("/v2",
                HandlerTypePredicate.forBasePackage("com.langthang.controller.v2"));
    }

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        if (enabledApiLimit) {
            ApiLimitInterceptor commonApiLimitIntercept = new ApiLimitInterceptor(apiBucketManager, 100, Duration.ofMinutes(1));
            registry.addInterceptor(commonApiLimitIntercept)
                    .addPathPatterns("/api/**")
                    .excludePathPatterns("/api/upload", "/api/auth/**");

            ApiLimitInterceptor uploadApiLimitIntercept = new ApiLimitInterceptor(apiBucketManager, 3, Duration.ofSeconds(10), "api-upload");
            registry.addInterceptor(uploadApiLimitIntercept)
                    .addPathPatterns("/api/upload");

            ApiLimitInterceptor authApiLimitIntercept = new ApiLimitInterceptor(apiBucketManager, 3, Duration.ofSeconds(5), "api-auth");
            registry.addInterceptor(authApiLimitIntercept)
                    .addPathPatterns("/api/auth/**");
        }
    }
}
