package com.github.mrchcat.explorewithme;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;


@Configuration
@RequiredArgsConstructor
public class Config {
    private final RestTemplateBuilder restTemplateBuilder;

    @Bean
    public RestTemplate getRestTemplate() {
        return restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(5))
                .build();
    }
}
