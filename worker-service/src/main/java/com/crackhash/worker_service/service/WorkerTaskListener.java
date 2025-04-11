package com.crackhash.worker_service.service;

import com.crackhash.common.model.WorkerTask;
import com.rabbitmq.client.Channel;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@AllArgsConstructor
public class WorkerTaskListener {
    private static final Logger logger = LoggerFactory.getLogger(WorkerTaskListener.class);

    private final WorkerService workerService;

    @RabbitListener(queues = "#{@environment.getProperty('rabbitmq.manager_to_worker_queue.name')}", ackMode = "MANUAL")
    public void receiveTask(@Payload WorkerTask task, Message message, Channel channel) {
        logger.info("Worker received task: {}", task);
        try {
            workerService.crackHash(task);

            if (channel.isOpen()) {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                logger.info("Task completed successfully for requestId: {}", task.getRequestId());
            } else {
                logger.error("Channel is closed, cannot acknowledge message with delivery tag {}", message.getMessageProperties().getDeliveryTag());
            }
        } catch (Exception e) {
            try {
                if (channel.isOpen()) {
                    channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
                }
                logger.error("Error processing task {}: {}", task.getRequestId(), e.getMessage(), e);
            } catch (IOException ioException) {
                logger.error("Error sending NACK for task {}: {}", task.getRequestId(), ioException.getMessage(), ioException);
            }
        }
    }
}
