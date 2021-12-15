package com.devjony.rabbitmqdemo.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class Consumer {

    private static final Logger logger = LoggerFactory.getLogger(Consumer.class);

    @RabbitListener(queues = {"${spring.rabbitmq.queue.name}"})
    public void receive(@Payload String fileBody) {
        logger.info("Body: " + fileBody);
    }
}
