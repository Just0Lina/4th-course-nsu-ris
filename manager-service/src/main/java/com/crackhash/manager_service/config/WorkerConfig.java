package com.crackhash.manager_service.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Getter
@EnableScheduling
@Configuration
public class WorkerConfig {

    @Value("${worker.count}")
    private int workerCount;
}