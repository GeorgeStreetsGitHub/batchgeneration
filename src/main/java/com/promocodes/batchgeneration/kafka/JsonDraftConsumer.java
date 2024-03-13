package com.promocodes.batchgeneration.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.kafka.broker.payload.Draft;
import com.promocodes.batchgeneration.config.BatchConfig;
import com.promocodes.batchgeneration.draftdb.PromoCodeRepository;
import com.promocodes.batchgeneration.util.BatchUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

@Service
public class JsonDraftConsumer {

    @Autowired
    ApplicationContext applicationContext;
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonDraftConsumer.class);

    @KafkaListener(topics = "draft-testing", groupId = "myGroup")
    public void consume(Draft draft){
        LOGGER.info(String.format("JSON DRAFT Message received -> %s", draft.toString()));
        BatchUtilities.createJsonDraft(draft);

        try {
            Job runJob = job();
            JobLauncher jobLauncher = getJobLauncher();
            JobParametersBuilder builder = new JobParametersBuilder();
            builder.addLong("time",System.currentTimeMillis()).toJobParameters();
            builder.addString("offerId", draft.getOfferId()).toJobParameters();
            jobLauncher.run(runJob, builder.toJobParameters());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private  JobLauncher getJobLauncher() {

        return applicationContext.getBean(JobLauncher.class);
    }

    private Job job(){
        return applicationContext.getBean("runJob", Job.class);
    }


}
