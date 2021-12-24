package com.devjony.rabbitmqdemo.config;

import com.devjony.rabbitmqdemo.rabbitmq.Consumer;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

    @Value("${spring.rabbitmq.failed.queue.name}")
    private String failedPaymentAnalysisQueue;

    @Autowired
    private ConnectionFactory connectionFactory;

    @Bean
    @Qualifier("paymentAnalysisQueue")
    public Queue paymentAnalysisQueue() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", exchangeName);
        arguments.put("x-dead-letter-routing-key", parkingPaymentAnalysisQueue);

        return new Queue(paymentAnalysisQueue, true, false, false, arguments);
    }

    @Bean
    @Qualifier("parkingPaymentAnalysisQueue")
    public Queue parkingPaymentAnalysisQueue() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", exchangeName);
        arguments.put("x-dead-letter-routing-key", paymentAnalysisQueue);

        return new Queue(parkingPaymentAnalysisQueue, true, false, false, arguments);
    }

    @Bean
    @Qualifier("failedPaymentAnalysisQueue")
    public Queue failedPaymentAnalysisQueue() {
        return new Queue(failedPaymentAnalysisQueue, true, false, false);
    }

    @Bean
    @Qualifier("exchange")
    public TopicExchange exchange() {
        return new TopicExchange(exchangeName, true, false);
    }

    @Bean
    @Qualifier("queueBind")
    Binding queueBind(Queue paymentAnalysisQueue, TopicExchange exchange) {
        return BindingBuilder.bind(paymentAnalysisQueue).to(exchange).with(this.paymentAnalysisQueue);
    }

    @Bean
    @Qualifier("parkingBind")
    Binding parkingBind(Queue parkingPaymentAnalysisQueue, TopicExchange exchange) {
        return BindingBuilder.bind(parkingPaymentAnalysisQueue).to(exchange).with(this.parkingPaymentAnalysisQueue);
    }

    @Bean
    @Qualifier("failedBind")
    Binding failedBind(Queue failedPaymentAnalysisQueue, TopicExchange exchange) {
        return BindingBuilder.bind(failedPaymentAnalysisQueue).to(exchange).with(this.failedPaymentAnalysisQueue);
    }

    @Bean
    public MessageListener consumer() {
        return new Consumer();
    }

    @Bean
    public SimpleMessageListenerContainer paymentAnalysisListenerContainer() {

        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();

        container.setConnectionFactory(connectionFactory);
        container.setQueues(paymentAnalysisQueue());
        container.setMessageListener(consumer());
        container.setDefaultRequeueRejected(false);

        return container;
    }
}
