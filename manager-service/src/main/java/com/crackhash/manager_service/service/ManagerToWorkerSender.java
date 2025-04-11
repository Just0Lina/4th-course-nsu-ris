package com.crackhash.manager_service.service;

import com.crackhash.common.model.WorkerTask;
import com.crackhash.manager_service.config.RabbitConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ManagerToWorkerSender {

    private final RabbitTemplate rabbitTemplate;
    @Value("${rabbitmq.manager_to_worker_queue.name}")
    private String managerToWorkerQueue;
    @Value("${rabbitmq.exchange.name}")
    private String directExchange;


    public void sendTask(WorkerTask task) {
        rabbitTemplate.convertAndSend(
                directExchange,
                managerToWorkerQueue,
                task,
                message -> {
                    message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                    return message;
                }
        );
    }
}
