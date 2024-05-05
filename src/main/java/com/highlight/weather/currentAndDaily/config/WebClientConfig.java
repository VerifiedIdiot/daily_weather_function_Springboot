package com.highlight.weather.currentAndDaily.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Autowired;

@Configuration
public class WebClientConfig {

    // 스프링이 자동으로 제공하는 WebClient.Builder를 주입받습니다.
    @Autowired
    private WebClient.Builder webClientBuilder;

    @Bean
    public WebClient webClient() {
        // 기본 URL 설정 없이 빌더를 사용하여 WebClient 인스턴스를 생성합니다.
        return webClientBuilder.build();
    }

//    @Bean
//    public WebClient webClient() {
//        return webClientBuilder
//                .baseUrl("https://api.example.com")  // 기본 URL 추가
//                .defaultHeader("Content-Type", "application/json")  // 기본 헤더 설정
//                .build();
//    }
}
