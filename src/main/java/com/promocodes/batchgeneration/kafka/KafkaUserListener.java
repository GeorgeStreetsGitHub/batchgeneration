package com.promocodes.batchgeneration.kafka;

import com.kafka.broker.payload.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaUserListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaUserListener.class);

    @KafkaListener(topics = "user-testing", groupId = "myGroup")
    public void consume(User user){

            LOGGER.info("in method CONSUMER USER TESTING...");
            LOGGER.info(String.format("JSON Message received -> %s", user.toString()));



    }
}
