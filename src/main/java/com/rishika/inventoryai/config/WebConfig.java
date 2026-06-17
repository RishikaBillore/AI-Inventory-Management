package com.rishika.inventoryai.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        // Force UTF-8 for String responses WITHOUT replacing Jackson (JSON converter).
        // configureMessageConverters() wipes the default list (including Jackson),
        // which is what caused the "Content-Type application/json not supported" error.
        // extendMessageConverters() adds to the existing list instead.
        converters.add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
    }
}