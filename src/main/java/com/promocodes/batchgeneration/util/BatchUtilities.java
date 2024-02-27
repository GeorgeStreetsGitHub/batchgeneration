package com.promocodes.batchgeneration.util;

import com.opencsv.CSVWriter;
import com.promocodes.batchgeneration.draftdb.PromoCodeEntity;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.protocol.Message;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class BatchUtilities {

    public final static Logger LOGGER = LoggerFactory.getLogger(BatchUtilities.class);
    /*public static void kafkaConsumer(){
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "myGroup");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class.getName());
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*"); // Allow deserialization of any class

        Consumer<String, Message> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList("user-testing"));

        while (true) {
            ConsumerRecords<String, Message> records = consumer.poll(Duration.ofMillis(1000));
            LOGGER.info("RECORDS " + records.toString());
            // Process the records
        }
    }*/

    public static LineMapper<PromoCodeEntity> lineMapper(){
        DefaultLineMapper<PromoCodeEntity> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("id", "name");

        BeanWrapperFieldSetMapper<PromoCodeEntity> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(PromoCodeEntity.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        return lineMapper;
    }

    public static boolean createPromoCodes(int totalCount, int length, int delimit, String prefix){

        String filePath = "src/main/resources/drafts.csv";

        try(CSVWriter csvWriter = new CSVWriter(
                new FileWriter(filePath))) {
            String[] promoCodes = new String[totalCount];
            String[] header = {"id", "promo-code"};
            csvWriter.writeNext(header);
            int id = 1;
            for(int i=0; i< promoCodes.length; i++){
                promoCodes[i] = prefix+randomPromoCode(length,delimit);
                String[] row = {String.valueOf(id),promoCodes[i]};
                id++;
                csvWriter.writeNext(row);

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    public static String randomPromoCode(int length, int delimit){
        int chunkSize = length/delimit;
        StringBuilder promoCode = new StringBuilder();

        for(int i=1; i<chunkSize; i++) {
            String generated = RandomStringUtils.random(delimit, true, true);
            if(i<chunkSize){
                promoCode.append("-");
            }
            promoCode.append(generated);

        }

        return String.valueOf(promoCode);
    }
}
