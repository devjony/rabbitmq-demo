package com.devjony.rabbitmqdemo.rabbitmq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class Consumer {

    Logger logger = Logger.getLogger(Consumer.class.getName());

    @RabbitListener(queues = {"${spring.rabbitmq.queue.name}"})
    public void receive(@Payload String fileBody) {
        logger.info("Body: " + fileBody);
    }
}
