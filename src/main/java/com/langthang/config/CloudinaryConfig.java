package com.langthang.config;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Value("${cloud.cloudinary.url}")
    private String connectUrl;

    @Bean
    public Cloudinary cloudinaryClient() {
        return new Cloudinary(connectUrl);
    }

}
