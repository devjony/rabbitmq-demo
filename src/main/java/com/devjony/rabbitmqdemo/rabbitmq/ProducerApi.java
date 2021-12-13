package com.devjony.rabbitmqdemo.rabbitmq;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class ProducerApi {

    private final Logger logger = Logger.getLogger(ProducerApi.class.getName());

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private Queue queue;

    public void send(String message) {

        try {
            rabbitTemplate.convertAndSend("send", message);
        } catch (Exception e) {
            logger.warning("Error sending message: " + e.getMessage());
        }
    }
}
