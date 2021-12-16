package com.devjony.rabbitmqdemo.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProducerApi {

    private static final Logger logger = LoggerFactory.getLogger(ProducerApi.class.getName());

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void send(String message) {

        try {
            rabbitTemplate.convertAndSend("exchange.demo","order_app.payment.analysis", message);
        } catch (Exception e) {
            logger.error("Error sending message: " + e.getMessage());
        }
    }
}
