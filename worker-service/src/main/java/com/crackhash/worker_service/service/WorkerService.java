package com.crackhash.worker_service.service;

import com.crackhash.common.model.WorkerResult;
import com.crackhash.common.model.WorkerTask;
import com.crackhash.worker_service.config.ManagerConfig;
import com.crackhash.worker_service.util.AlphabetGenerator;
import com.crackhash.worker_service.util.MD5Util;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkerService {
    private static final Logger logger = LoggerFactory.getLogger(WorkerService.class);

    @Autowired
    private RestTemplate restTemplate = new RestTemplate();
    private final ManagerConfig managerConfig;

    public void crackHash(WorkerTask task) {
        logger.info("Starting hash cracking for requestId: {} with task: {}", task.getRequestId(), task);
        List<String> results = new ArrayList<>();
        AlphabetGenerator generator = new AlphabetGenerator(task.getMaxLength());
        try {
            for (String word : generator.getPart(task.getPartNumber(), task.getPartCount())) {
                logger.debug("Checking word: {}", word);
                if (MD5Util.hash(word).equals(task.getHash())) {
                    logger.info("Found matching word: {}", word);
                    results.add(word);
                }
            }
        } catch (Exception e) {
            logger.error("Error while generating combinations or cracking hash for requestId: {}", task.getRequestId(), e);
        }

        if (results.isEmpty()) {
            logger.info("No matching words found for requestId: {}", task.getRequestId());
        }
        HttpHeaders headers = new HttpHeaders();

        WorkerResult result = new WorkerResult(task.getRequestId(), results);
        try {
            HttpEntity<WorkerResult> entity = new HttpEntity<>(result, headers);
            restTemplate.exchange(managerConfig.getManagerUrl(), HttpMethod.PATCH, entity, Void.class);
        } catch (Exception e) {
            logger.error("Error while sending results for requestId: {}", task.getRequestId(), e);
        }
        logger.info("Finished hash cracking for requestId: {}", task.getRequestId());
    }
}
