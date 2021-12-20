package com.devjony.rabbitmqdemo.rabbitmq;

import com.rabbitmq.client.impl.AMQImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.AmqpNackReceivedException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Consumer extends AbstractRetryMessageListener {

    private static final Logger logger = LoggerFactory.getLogger(Consumer.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    protected void doOnMessage(Message message) {
        logger.info("Body: " + new String(message.getBody()));
        try {
            throw new Exception("Teste retry");
        } catch (Exception e) {
//            rabbitTemplate.send("moip","order_app.payment.analysis.parking", message);
        }
    }
}
