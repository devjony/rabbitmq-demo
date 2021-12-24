package com.devjony.rabbitmqdemo.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ProducerApi {

    private static final Logger logger = LoggerFactory.getLogger(ProducerApi.class);

    @Value("${spring.rabbitmq.exchange.name}")
    String exchangeName;

    @Value("${spring.rabbitmq.queue.name}")
    String queueName;

    @Autowired
    private AmqpTemplate rabbitTemplate;

    public void send(String message) {

        try {
            rabbitTemplate.convertAndSend(exchangeName, queueName, message);
        } catch (Exception e) {
            logger.error("Error sending message: " + e.getMessage());
        }
    }
}
