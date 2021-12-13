package com.devjony.rabbitmqdemo.rabbitmq;

import com.rabbitmq.client.Connection;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.connection.SimpleRoutingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.stereotype.Component;


import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import static org.springframework.amqp.core.BindingBuilder.bind;

@Component
public class Consumer {

    Logger logger = Logger.getLogger(Consumer.class.getName());

    @Autowired
    private AmqpAdmin amqpAdmin;

    private ConnectionFactory connectionFactory = new SimpleRoutingConnectionFactory();

    @Value("${spring.rabbitmq.queue.name}")
    private String queueName;

    @Value("${spring.rabbitmq.wait.queue.name}")
    private String waitQueueName;

    @Value("${spring.rabbitmq.exchange.retry1.name}")
    private String exchangeRetry1Name;


//    @RabbitListener(queues = {"${spring.rabbitmq.queue.name}"}, ackMode = "MANUAL")
//    public void receive(@Payload Message fileBody) throws Exception{
//
//        ChannelAwareMessageListener ch = new ChannelAwareMessageListener() {
//            @Override
//            public void onMessage(Message message, Channel channel) throws Exception {
//                logger.info(fileBody.getMessageProperties().toString());
//                logger.info(fileBody.getMessageProperties().getConsumerQueue());
//                logger.info(fileBody.getMessageProperties().getHeader("x-retries").toString());
//                logger.info("Body: " + fileBody.getBody());
//                channel.basicAck(1,true);
//            }
//        };
//
//        ch.onMessage(fileBody);
//    }

    @Bean
    public SimpleMessageListenerContainer receive() throws IOException, TimeoutException {
        SimpleMessageListenerContainer failedContainer = new SimpleMessageListenerContainer(connectionFactory);
        failedContainer.setConnectionFactory(connectionFactory);
        failedContainer.setQueueNames(queueName);

        failedContainer.setMessageListener(new MessageListener() {

            @Override
            public void onMessage(final Message message) {
                try {
                    message.getMessageProperties().setExpiration("5000");
                    message.getMessageProperties().setHeader("x-retries", "1");
                    RabbitTemplate template = new RabbitTemplate();
                    template.send(exchangeRetry1Name, waitQueueName, message);
                } catch (Exception e) {
                    System.out.println(e);
                    logger.info("Error: " + e);
                }
            }
        });
        failedContainer.setTaskExecutor(new SimpleAsyncTaskExecutor("FailedPaymentAnalysisTaskExecutor-"));

        return failedContainer;
    }
}
