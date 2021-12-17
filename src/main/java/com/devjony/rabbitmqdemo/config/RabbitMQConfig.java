package com.devjony.rabbitmqdemo.config;

import com.devjony.rabbitmqdemo.rabbitmq.Consumer;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.PooledChannelConnectionFactory;
import org.springframework.amqp.rabbit.connection.SimpleRoutingConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.SimpleAsyncTaskExecutor;


import java.util.HashMap;
import java.util.Map;

import static org.springframework.amqp.core.BindingBuilder.bind;

@Configuration
public class RabbitMQConfig {

    @Value("${spring.rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${spring.rabbitmq.queue.name}")
    private String paymentAnalysisQueue;

    @Value("${spring.rabbitmq.parking.queue.name}")
    private String parkingPaymentAnalysisQueue;

    @Autowired
    private AmqpAdmin amqpAdmin;

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
    @Qualifier("exchange")
    public TopicExchange exchange() {
        return new TopicExchange(exchangeName, true, false);
    }

    @Bean
    @Qualifier("queueBind")
    Binding queueBind(Queue paymentAnalysisQueue, TopicExchange exchange) {
        return BindingBuilder.bind(paymentAnalysisQueue).to(exchange).with("order_app.payment.analysis");
    }

    @Bean
    public SimpleMessageListenerContainer paymentAnalysisListenerContainer() {

        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();

        container.setConnectionFactory(connectionFactory);
        container.setQueues(paymentAnalysisQueue());

//        container.setConcurrentConsumers(Integer.valueOf(connectionProperties.getMaxListeners()));
        container.setMessageListener(new Consumer());
//        container.setTaskExecutor(new SimpleAsyncTaskExecutor("PaymentAnalysisTaskExecutor-"));
        container.setDefaultRequeueRejected(false);

        TopicExchange exchange = new TopicExchange(exchangeName);
        amqpAdmin.declareExchange(exchange);
        amqpAdmin.declareQueue(paymentAnalysisQueue());
//        amqpAdmin.declareQueue(failedParkingPaymentAnalysisQueue());
//        amqpAdmin.declareBinding(bind(paymentAnalysisQueue()).to(exchange).with(RoutingKeys.PAYMENTV2_ANALYSIS.getKey())); // Quando payment entra em análise
//        amqpAdmin.declareBinding(bind(paymentAnalysisQueue()).to(exchange).with(QUEUE_NAME)); // retentativa de um payment que falhou
//        amqpAdmin.declareBinding(bind(failedParkingPaymentAnalysisQueue()).to(exchange).with(FAILED_PARKING_QUEUE_NAME)); // fica parado por 60 segundos antes de re-tentar

        return container;
    }

}
