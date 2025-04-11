package com.crackhash.worker_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Value("${rabbitmq.manager_to_worker_queue.name}")
    private String managerToWorkerQueue;

    @Value("${rabbitmq.worker_to_manager_queue.name}")
    private String workerToManagerQueue;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(exchangeName);
    }

    @Bean
    public Queue managerToWorkerQueue() {
        return new Queue(managerToWorkerQueue, true);
    }

    @Bean
    public Queue workerToManagerQueue() {
        return new Queue(workerToManagerQueue, true);
    }

    @Bean
    public Binding bindingManagerToWorker() {
        return BindingBuilder.bind(managerToWorkerQueue()).to(directExchange()).with(managerToWorkerQueue);
    }

    @Bean
    public Binding bindingWorkerToManager() {
        return BindingBuilder.bind(workerToManagerQueue()).to(directExchange()).with(workerToManagerQueue);
    }

    @Bean
    public MessageConverter jackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2MessageConverter());

        return rabbitTemplate;
    }
}
