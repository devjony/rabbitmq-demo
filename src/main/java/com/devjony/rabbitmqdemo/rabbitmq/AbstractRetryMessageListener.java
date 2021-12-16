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

        MessageDecorator messageDecorator = new MessageDecorator(message);

        LOGGER.info("Message incoming... deliveryCount={} routingKey={} mpa={} channel={}",
                messageDecorator.getDeliveryCount(), messageDecorator.getRoutingKey(), messageDecorator.getMpa(),
                messageDecorator.getChannel());

        doOnMessage(messageDecorator);
    }

    protected abstract void doOnMessage(final MessageDecorator messageDecorator);
}
