package com.crackhash.manager_service.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class WorkerConfig {
    @Value("${worker.url}")
    private String workerUrl;

    @Value("${worker.count}")
    private int workerCount;
}