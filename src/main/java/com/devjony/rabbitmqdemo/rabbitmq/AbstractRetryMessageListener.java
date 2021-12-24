package com.devjony.rabbitmqdemo.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

import java.util.Map;

public abstract class AbstractRetryMessageListener implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRetryMessageListener.class);

    @Override
    public final void onMessage(Message message) {

        Map<String, Object> messageHeaders = message.getMessageProperties().getHeaders();
        Integer retryHeader = (Integer) messageHeaders.get("x-retry");;

        if (retryHeader == null) {
            retryHeader = 0;
            message.getMessageProperties().setHeader("x-retry", retryHeader);
        } else if (retryHeader >= 5) {
            LOGGER.warn("Message failed to be consumed too many times, sending to failed queue");
            message.getMessageProperties().setExpiration(null);
            doOnMessageFail(message);
        } else {
            message.getMessageProperties().setHeader("x-retry", ++retryHeader);
        }

        String expiration = String.valueOf(retryHeader * 2000);
        message.getMessageProperties().setExpiration(expiration);

        LOGGER.info("Message incoming... retry={} expiration={}", retryHeader, expiration);
        doOnMessage(message);
    }

    protected abstract void doOnMessage(final Message message);
    protected abstract void doOnMessageFail(final Message message);
}
