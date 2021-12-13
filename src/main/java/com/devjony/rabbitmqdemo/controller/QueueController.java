package com.devjony.rabbitmqdemo.controller;

import com.devjony.rabbitmqdemo.rabbitmq.ProducerApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
public class QueueController {

    private final Logger logger = Logger.getLogger(QueueController.class.getName());

    @Autowired
    ProducerApi producerApi;

    @PostMapping("/send")
    ResponseEntity<String> send(@RequestBody String message) {
        logger.info("New request received: [/send]" + message.toString());

        try {
            producerApi.send(message);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("No");
        }

        return ResponseEntity.ok().body("Yes");
    }
}
