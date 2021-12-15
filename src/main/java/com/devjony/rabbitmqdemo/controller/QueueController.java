package com.devjony.rabbitmqdemo.controller;

import com.devjony.rabbitmqdemo.rabbitmq.ProducerApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QueueController {

    private static final Logger logger = LoggerFactory.getLogger(QueueController.class);

    @Autowired
    ProducerApi producerApi;

    @PostMapping("/send")
    ResponseEntity<String> send(@RequestBody String message) {
        logger.info("New request received: [/send]" + message);

        try {
            producerApi.send(message);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("No");
        }

        return ResponseEntity.ok().body("Yes");
    }
}
