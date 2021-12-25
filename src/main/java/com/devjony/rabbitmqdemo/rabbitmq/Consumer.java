package com.devjony.rabbitmqdemo.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class Consumer extends AbstractRetryMessageListener {

    private static final Logger logger = LoggerFactory.getLogger(Consumer.class);

    @Value("${spring.rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${spring.rabbitmq.parking.queue.name}")
    private String parkingQueueName;

    @Value("${spring.rabbitmq.failed.queue.name}")
    private String failedQueueName;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Override
    protected void doOnMessage(Message message) {
        Integer retries = message.getMessageProperties().getHeader("x-retry");

        if(retries < 5) {
            rabbitTemplate.convertAndSend(exchangeName, parkingQueueName, message);
        } else {
            logger.info("max retries reached");
            message.getMessageProperties().setExpiration(null);
            rabbitTemplate.convertAndSend(exchangeName, failedQueueName, message);
        }
    }

    @Override
    protected void doOnMessageFail(Message message) {
        rabbitTemplate.convertAndSend(exchangeName, failedQueueName, message);
    }
}
