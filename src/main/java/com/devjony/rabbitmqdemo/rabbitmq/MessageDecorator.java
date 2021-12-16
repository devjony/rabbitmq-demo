package com.devjony.rabbitmqdemo.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;

import java.util.List;
import java.util.Map;

public class MessageDecorator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageDecorator.class);

    private final Message message;

    public MessageDecorator(final Message message) {
        if (message == null) {
            throw new IllegalArgumentException("message must not be null");
        }

        this.message = message;
    }

    public String getBody() {
        return new String(message.getBody());
    }

    public String getHeaders() {
        return message.getMessageProperties().getHeaders().toString();
    }

    public Integer getDeliveryCount() {
        if (!message.getMessageProperties().getHeaders().containsKey("x-death")) {
            return 1;
        }

        try {
            List<Map<String, Object>> xDeath = (List<Map<String, Object>>) message.getMessageProperties().getHeaders().get("x-death");
            Map<String, Object> firstXDeath = xDeath.get(0);


            if (firstXDeath.containsKey("count")) {
                return ((Long) firstXDeath.get("count")).intValue();
            }

            return xDeath.size();
        } catch(Exception e) {
            LOGGER.warn("Couldn't get message delivery count. Error: " + e.getMessage());
        }

        return 1;
    }

    public String getRoutingKey() {
        return message.getMessageProperties().getReceivedRoutingKey();
    }

    public String getMpa() {
        if (message.getMessageProperties().getHeaders().containsKey("Mpa")) {
            try {
                return (String) message.getMessageProperties().getHeaders().get("Mpa");
            } catch(Exception e) {
                LOGGER.warn("Couldn't extract Mpa from Message Headers");
                return null;
            }
        }

        return null;
    }

    public String getChannel() {
        if (message.getMessageProperties().getHeaders().containsKey("Channel")) {
            try {
                return (String) message.getMessageProperties().getHeaders().get("Channel");
            } catch(Exception e) {
                LOGGER.warn("Couldn't extract Mpa from Message Headers");
                return null;
            }
        }

        return null;
    }

}
