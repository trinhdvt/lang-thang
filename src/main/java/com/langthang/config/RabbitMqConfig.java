package com.langthang.config;


import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String QK_EXAMPLE_QUEUE = "exampleQueue";
    public static final String QK_MESSAGE_QUEUE = "messageQueue";

    @Bean
    public RabbitTemplate jsonRabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonConverter());
        return template;
    }

    @Bean
    public MessageConverter jsonConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean("exampleQueue")
    public Queue exampleQueue() {
        return new Queue(QK_EXAMPLE_QUEUE);
    }

    @Bean("messageQueue")
    public Queue messageQueue() {
        return new Queue(QK_MESSAGE_QUEUE);
    }
}
