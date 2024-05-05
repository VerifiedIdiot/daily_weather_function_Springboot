package com.highlight.weather.currentAndDaily.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "weatherTaskExecutor")
    public Executor weatherTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // CorePoolSize: 스레드 풀이 유지할 최소 스레드 수입니다.
        // 작업 수가 이 수를 초과하면 스레드가 추가로 생성됩니다.
        executor.setCorePoolSize(5);

        // MaxPoolSize: 스레드 풀이 관리할 수 있는 최대 스레드 수입니다.
        // 코어 스레드가 바쁠 때 추가 스레드가 생성되어 이 값을 최대로 할 수 있습니다.
        executor.setMaxPoolSize(10);

        // QueueCapacity: 작업 대기열의 용량입니다. 이 큐가 가득 차면,
        // 새로운 작업을 위해 새 스레드가 생성됩니다(최대 MaxPoolSize까지).
        executor.setQueueCapacity(50);

        // ThreadNamePrefix: 생성되는 스레드의 이름에 접두사를 추가합니다.
        // 이를 통해 로그 파일 등에서 스레드를 쉽게 식별할 수 있습니다.
        executor.setThreadNamePrefix("WeatherAsync-");

        // initialize: 스레드 풀을 초기화하고, 설정된 구성에 따라 스레드를 준비합니다.
        executor.initialize();

        return executor;
    }
}

