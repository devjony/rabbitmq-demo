package com.devjony.rabbitmqdemo.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Consumer extends AbstractRetryMessageListener {

    private static final Logger logger = LoggerFactory.getLogger(Consumer.class);

    @Override
    protected void doOnMessage(MessageDecorator messageDecorator) {
        logger.info("Body: " + messageDecorator.getBody());
    }
}
