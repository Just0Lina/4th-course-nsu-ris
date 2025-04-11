package com.crackhash.manager_service.service;

import com.crackhash.common.model.WorkerResult;
import com.crackhash.manager_service.config.RabbitConfig;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class WorkerToManagerListener {

    private final ManagerService managerService;
    private static final Logger logger = LoggerFactory.getLogger(WorkerToManagerListener.class);


    @RabbitListener(queues = "#{@environment.getProperty('rabbitmq.worker_to_manager_queue.name')}", ackMode = "MANUAL")
    public void handleWorkerResult(@Payload WorkerResult result, Message message, Channel channel) {
        try {
            managerService.processWorkerResult(result);
            if (channel.isOpen()) {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                logger.info("Task completed successfully for requestId: {}", result.getRequestId());
            } else {
                logger.error("Channel is closed, cannot acknowledge message with delivery tag {}", message.getMessageProperties().getDeliveryTag());
            }
        } catch (Exception e) {
            try {
                if (channel.isOpen()) {
                    channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
                }
                logger.error("Error processing task {}: {}", result.getRequestId(), e.getMessage(), e);
            } catch (IOException ioException) {
                logger.error("Error sending NACK for task {}: {}", result.getRequestId(), ioException.getMessage(), ioException);
            }
        }
    }
}
