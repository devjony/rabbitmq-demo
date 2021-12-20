package com.devjony.rabbitmqdemo.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

public abstract class AbstractRetryMessageListener implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRetryMessageListener.class);

    private static final Integer MAX_DELIVERIES = 10;

    @Override
    public final void onMessage(Message message) {
//        MessageDecorator messageDecorator = new MessageDecorator(message);
        Integer retryHeader = message.getMessageProperties().getHeader("x-retry");
        if (retryHeader == null) {
            retryHeader = 1;
        } else {
            retryHeader = Integer.valueOf(retryHeader) + 1;
        }

        LOGGER.info("Message incoming... retry={}", retryHeader);


        String expiration = String.valueOf(retryHeader * 5000);
        message.getMessageProperties().setExpiration(expiration);
        message.getMessageProperties().setHeader("x-retry", retryHeader);

        LOGGER.info("Expiration: {}", expiration);
        doOnMessage(message);
    }

    protected abstract void doOnMessage(final Message message);
}
