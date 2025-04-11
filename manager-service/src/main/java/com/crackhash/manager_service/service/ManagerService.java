package com.crackhash.manager_service.service;

import com.crackhash.common.model.HashRequest;
import com.crackhash.common.model.HashStatus;
import com.crackhash.common.model.Status;
import com.crackhash.common.model.TaskStatus;
import com.crackhash.common.model.WorkerTask;
import com.crackhash.common.model.WorkerResult;
import com.crackhash.manager_service.config.RabbitConfig;
import com.crackhash.manager_service.config.WorkerConfig;
import com.crackhash.manager_service.mapper.HashRequestMapper;
import com.crackhash.manager_service.model.HashRequestFullInfo;
import com.crackhash.manager_service.repository.HashRequestRepository;
import com.crackhash.manager_service.repository.WorkerTaskRepository;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.Store;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    private final HashRequestRepository hashRequestRepository;
    private final WorkerTaskRepository workerTaskRepository;
    private final HashRequestMapper hashRequestMapper;
    private final ManagerToWorkerSender managerToWorkerSender;


    @Transactional
    public String processRequest(HashRequest request) {
        String requestId = UUID.randomUUID().toString();
        logger.info("Received new hash cracking request with requestId: {}", requestId);
        saveRequestToDatabase(requestId, request);

        sendTasksToWorkers(requestId, request);
        return requestId;
    }

    private void sendTasksToWorkers(String requestId, HashRequest request) {
        for (int i = 0; i < workerConfig.getWorkerCount(); i++) {
            WorkerTask workerTask = new WorkerTask(requestId, request.getHash(), request.getMaxLength(), i, workerConfig.getWorkerCount());
            try {
                logger.info("Sending task {} for hash {} to worker №{} to MANAGER_TO_WORKER_QUEUE", workerTask, workerTask.getHash(), workerTask.getPartCount());
                sendTask(workerTask);
            } catch (Exception e) {
                saveTaskToDatabase(workerTask);
                logger.error("Error sending task to worker: {}", e.getMessage());
            }
        }
    }

    private void saveRequestToDatabase(String requestId, HashRequest request) {
        HashRequestFullInfo hashRequest = new HashRequestFullInfo(requestId, request.getHash(), request.getMaxLength(), Status.IN_PROGRESS, workerConfig.getWorkerCount());
        hashRequestRepository.save(hashRequest);
    }

    @Async
    protected void sendTask(WorkerTask task) throws Exception {
      managerToWorkerSender.sendTask(task);
    }

    public void saveTaskToDatabase(WorkerTask task) {
        task.setStatus(TaskStatus.PENDING);

        WorkerTask existingTask = workerTaskRepository.findByRequestIdAndPartNumber(task.getRequestId(),task.getPartNumber()).orElse(null);

        if (existingTask == null) {
            try {
                workerTaskRepository.save(task);
                logger.info("Task saved to database: {}", task);
            } catch (Exception e) {
                logger.error("Error saving task to database: {}", task, e);
            }
        } else {
            logger.info("Task already exists in database: {}", task);
        }
    }

    @Scheduled(fixedRate = 6000)
    @Transactional
    public void resendFailedTasks() {
        List<WorkerTask> pendingTasks = workerTaskRepository.findByStatus(TaskStatus.PENDING);

        for (WorkerTask task : pendingTasks) {
            try {
                sendTask(task);
                logger.info("Task resent to queue: {}", task);

                workerTaskRepository.deleteByRequestIdAndPartNumber(task.getRequestId(), task.getPartNumber());
                logger.info("Task deleted from database after being sent: {}", task);
            } catch (Exception e) {
                logger.error("Error resending task {}: {}", task.getRequestId(), e.getMessage());
            }
        }
    }

    public HashStatus getRequestStatus(String requestId) {
        Optional<HashRequestFullInfo> optionalHashRequest = hashRequestRepository.findById(requestId);
        HashStatus status = optionalHashRequest
                .map(hashRequest -> new HashStatus(hashRequest.getStatus(), hashRequest.getFoundWords()))
                .orElse(new HashStatus(Status.ERROR, null));

        logger.info("Request status for requestId {}: {}", requestId, status.getStatus());
        return status;
    }

    @Transactional
    public void processWorkerResult(WorkerResult result) {
        logger.info("Received worker result for requestId: {}", result.getRequestId());
        HashRequestFullInfo hashRequestFullInfo = hashRequestRepository.findById(result.getRequestId()).orElseThrow(() -> new IllegalStateException(
                "HashRequestFullInfo not found for requestId: " + result.getRequestId())
        );

        hashRequestMapper.updateHashRequestFullInfo(hashRequestFullInfo, result);

        hashRequestRepository.save(hashRequestFullInfo);
        logger.info("Updated existing request in database for requestId: {}", result.getRequestId());
    }
}
