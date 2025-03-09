package com.crackhash.manager_service.service;

import com.crackhash.common.model.HashRequest;
import com.crackhash.common.model.HashStatus;
import com.crackhash.common.model.Status;
import com.crackhash.common.model.WorkerTask;
import com.crackhash.common.model.WorkerResult;
import com.crackhash.manager_service.config.WorkerConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
public class ManagerService {
    private static final Logger logger = LoggerFactory.getLogger(ManagerService.class);
    private final WorkerConfig workerConfig;
    private final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private final Map<String, HashStatus> requests = new ConcurrentHashMap<>();
    private final RestTemplate restTemplate = new RestTemplate();

    public String processRequest(HashRequest request) {
        String requestId = UUID.randomUUID().toString();
        logger.info("Received new hash cracking request with requestId: {}", requestId);

        requests.put(requestId, new HashStatus(Status.IN_PROGRESS, new ArrayList<>()));

        for (int i = 0; i < workerConfig.getWorkerCount(); i++) {

            WorkerTask workerTask = new WorkerTask(requestId, request.getHash(), request.getMaxLength(), i, workerConfig.getWorkerCount());
            int finalI = i;
            executorService.submit(() -> sendTask(workerTask, requestId, finalI));

        }

        return requestId;
    }

    @Async
    protected void sendTask(WorkerTask task, String requestId, int workerIndex) {
        try {
            logger.info("Sending task {} for hash {} to worker №{}, total workers", task, task.getHash(), workerIndex, task.getPartCount());
            restTemplate.postForEntity(workerConfig.getWorkerUrl(), task, Void.class);
        } catch (Exception e) {
            logger.error("Error sending task to worker № {} for requestId {}: {}", workerIndex, requestId, e.getMessage(), e);
        }
    }

    public HashStatus getRequestStatus(String requestId) {
        HashStatus status = requests.getOrDefault(requestId, new HashStatus(Status.ERROR, null));
        logger.info("Request status for requestId {}: {}", requestId, status.getStatus());
        return status;
    }

    public void processWorkerResult(WorkerResult result) {
        requests.computeIfPresent(result.getRequestId(), (requestId, status) -> {
            synchronized (status) {
                status.getData().addAll(result.getData());
                if (!status.getData().isEmpty()) {
                    status.setStatus(Status.READY);
                    logger.info("Received results for requestId {}, marking status as READY", requestId);
                }
            }
            return status;
        });

        if (!requests.containsKey(result.getRequestId())) {
            logger.warn("Received result for unknown requestId: {}", result.getRequestId());
        }
    }

}
