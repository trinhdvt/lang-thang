package com.langthang.event.services;

import com.langthang.config.RabbitMqConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MessageListener {
    @RabbitListener(queues = RabbitMqConfig.QK_EXAMPLE_QUEUE)
    public void onMessageReceived(String message) {
        log.debug("Message received in QK_EXAMPLE_QUEUE!: " + message);
    }

    @RabbitListener(queues = RabbitMqConfig.QK_MESSAGE_QUEUE)
    public void onMessageReceived2(String message) {
        log.debug("Message received in QK_MESSAGE_QUEUE!: " + message);
    }

}