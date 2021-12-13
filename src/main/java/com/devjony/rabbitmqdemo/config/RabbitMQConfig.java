package com.devjony.rabbitmqdemo.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    @Value("${spring.rabbitmq.exchange.name}")
    private String demoExchangeName;

    @Value("${spring.rabbitmq.queue.name}")
    private String queueName;

    @Value("${spring.rabbitmq.wait.queue.name}")
    private String waitQueueName;

    @Value("${spring.rabbitmq.exchange.retry1.name}")
    private String exchangeRetry1Name;

    @Value("${spring.rabbitmq.exchange.retry2.name}")
    private String exchangeRetry2Name;

    @Bean
    @Qualifier("demoExchange")
    public DirectExchange demoExchange() {
        return new DirectExchange(demoExchangeName, true, false);
    }

    @Bean
    @Qualifier("queue")
    public Queue queue() {
        return new Queue(queueName, true);
    }

    @Bean
    @Qualifier("exchangeRetry1")
    public DirectExchange exchangeRetry1() {
        return new DirectExchange(exchangeRetry1Name, true, false);
    }

    @Bean
    @Qualifier("waitQueue")
    public Queue waitQueue() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", exchangeRetry2Name);

        return new Queue(waitQueueName, true);
    }

    @Bean
    @Qualifier("exchangeRetry2")
    public DirectExchange exchangeRetry2() {
        return new DirectExchange(exchangeRetry2Name, true, false);
    }

    @Bean
    Binding queueBind(Queue queue, DirectExchange demoExchange) {
        return BindingBuilder.bind(queue).to(demoExchange).with("send");
    }

    @Bean
    Binding waitQueueBind(Queue waitQueue, DirectExchange exchangeRetry1) {
        return BindingBuilder.bind(waitQueue).to(exchangeRetry1).with(queueName);
    }

    @Bean
    Binding retryQueuBind(Queue queue, DirectExchange exchangeRetry2) {
        return BindingBuilder.bind(queue).to(exchangeRetry2).with(queueName);
    }
}
