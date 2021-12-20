package com.devjony.rabbitmqdemo.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

public class Consumer extends AbstractRetryMessageListener {

    private static final Logger logger = LoggerFactory.getLogger(Consumer.class);

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Override
    protected void doOnMessage(Message message) {
        Integer retries = message.getMessageProperties().getHeader("x-retry");

        if(retries <= 3) {
            rabbitTemplate.convertAndSend("moip","order_app.payment.analysis.parking", message);
        } else {
            logger.info("max retries reached");
            message.getMessageProperties().setExpiration(null);
            rabbitTemplate.convertAndSend("moip", "order_app.payment.analysis.dlq", message);
        }
    }
}
