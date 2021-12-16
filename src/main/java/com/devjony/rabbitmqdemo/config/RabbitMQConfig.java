package com.devjony.rabbitmqdemo.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    @Value("${spring.rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${spring.rabbitmq.queue.name}")
    private String paymentAnalysisQueue;

    @Value("${spring.rabbitmq.parking.queue.name}")
    private String parkingPaymentAnalysisQueue;

    @Bean
    public Queue paymentAnalysisQueue() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", exchangeName);
        arguments.put("x-dead-letter-routing-key", parkingPaymentAnalysisQueue);

        return new Queue(paymentAnalysisQueue, true, false, false, arguments);
    }

    @Bean
    public Queue parkingPaymentAnalysisQueue() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", exchangeName);
        arguments.put("x-dead-letter-routing-key", paymentAnalysisQueue);

        return new Queue(parkingPaymentAnalysisQueue, true, false, false, arguments);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(exchangeName, true, false);
    }

    @Bean
    Binding queueBind(Queue paymentAnalysisQueue, TopicExchange exchange) {
        return BindingBuilder.bind(paymentAnalysisQueue).to(exchange).with("order_app.payment.analysis");
    }
}
