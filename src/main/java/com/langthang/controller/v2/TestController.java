package com.langthang.controller.v2;

import com.langthang.config.RabbitMqConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;

@RestController
@RequestMapping("/test")
public class TestController {

    private final RabbitTemplate rabbitTemplate;

    public TestController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @GetMapping("/event")
    ResponseEntity<Void> postEventMessage() {
        final String timeNowMessage = String.format("%s - %s", "ExampleMessage", getTimeNowRepresentation());
        rabbitTemplate.convertAndSend(RabbitMqConfig.QK_EXAMPLE_QUEUE, timeNowMessage);
        rabbitTemplate.convertAndSend(RabbitMqConfig.QK_MESSAGE_QUEUE, timeNowMessage);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private String getTimeNowRepresentation() {
        long now = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");
        return simpleDateFormat.format(now);
    }

}
