package com.crackhash.worker_service.service;

import com.crackhash.common.model.WorkerResult;
import com.crackhash.common.model.WorkerTask;
import com.crackhash.worker_service.util.AlphabetGenerator;
import com.crackhash.worker_service.util.MD5Util;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkerService {
    private static final Logger logger = LoggerFactory.getLogger(WorkerService.class);
    private final RabbitTemplate rabbitTemplate;
    @Value("${rabbitmq.worker_to_manager_queue.name}")
    private String workerToManagerQueue;
    @Value("${rabbitmq.exchange.name}")
    private String directExchange;


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

        WorkerResult result = new WorkerResult(task.getRequestId(), results);
        try {
            sendTask(result);
        } catch (Exception e) {
            logger.error("Error while sending results for requestId: {}", task.getRequestId(), e);
        }
        logger.info("Finished hash cracking for requestId: {}", task.getRequestId());
    }

    public void sendTask(WorkerResult task) {
        rabbitTemplate.convertAndSend(
                directExchange,
                workerToManagerQueue,
                task,
                message -> {
                    message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                    return message;
                }
        );
    }
}
