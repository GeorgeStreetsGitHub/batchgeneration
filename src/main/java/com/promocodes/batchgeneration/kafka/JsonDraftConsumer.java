package com.promocodes.batchgeneration.kafka;

import com.promocodes.batchgeneration.draftdb.JsonDraftEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class JsonDraftConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonDraftConsumer.class);

    /*@KafkaListener(topics = "", groupId = "myGroup")
    public void consume(){
        LOGGER.info(String.format("JSON-Message received -> %s"));
    }*/
}
