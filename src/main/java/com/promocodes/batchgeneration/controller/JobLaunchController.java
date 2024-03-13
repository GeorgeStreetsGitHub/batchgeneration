package com.promocodes.batchgeneration.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.promocodes.batchgeneration.draftdb.PromoCodeEntity;
import com.promocodes.batchgeneration.util.BatchUtilities;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/drafts")
@RequiredArgsConstructor
public class JobLaunchController {

    private final JobRepository jobRepository;
    private final JobLauncher jobLauncher;
    @Autowired
    private Job job;

    @GetMapping("launch/{id}")
    public void handle(@PathVariable("id") String id){
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("param", id)
                .addString("time", LocalDateTime.now().toString())
                .toJobParameters();
        try {
            jobLauncher.run(job, jobParameters);

        } catch (JobExecutionAlreadyRunningException
                | JobRestartException
                | JobInstanceAlreadyCompleteException
                | JobParametersInvalidException e) {
            e.printStackTrace();

        }
    }

    @GetMapping("/jsonlaunch")
    public void jsonLaunch(){
        final String jsonPath = "src/main/resources/draft.json";
        final String csvPath = "src/main/resources/promocodes.csv";
        final ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode jsonNode = objectMapper.readTree(new File(jsonPath));
            //System.out.println("totalCount " + jsonNode.get("draft").get("totalCount"));
            /*BatchUtilities.createPromoCodes(
                    jsonNode.get("draft").get("totalCount").intValue(),
                    jsonNode.get("draft").get("definitionTemplate").get("length").intValue(),
                    jsonNode.get("draft").get("definitionTemplate").get("delimitLength").intValue(),
                    jsonNode.get("draft").get("definitionTemplate").get("prefix").asText()*/
            Long totalCount =jsonNode.get("draft").get("totalCount").longValue();
            Long length = jsonNode.get("draft").get("definitionTemplate").get("length").longValue();
            Long delimitLength = jsonNode.get("draft").get("definitionTemplate").get("delimitLength").longValue();
            String prefix = jsonNode.get("draft").get("definitionTemplate").get("prefix").asText();
            String offerId = jsonNode.get("draft").get("offerId").asText();

            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("time", LocalDateTime.now().toString())
                    .addString("OfferID", offerId)
                    .toJobParameters();

            JobExecution jobExecution = new JobExecution(Long.valueOf(1));

            jobLauncher.run(job, jobParameters);


            // Create CSV to dump promo codes generated.
           /* FlatFileItemReader<PromoCodeEntity> jsonReader = new FlatFileItemReader<>();
            jsonReader.setResource(new FileSystemResource(csvPath));
            jsonReader.setName("jsonReader");
            jsonReader.setLinesToSkip(1);
            jsonReader.setLineMapper(BatchUtilities.lineMapper());*/

        } catch (IOException
                 | JobExecutionAlreadyRunningException
                 | JobRestartException
                 | JobInstanceAlreadyCompleteException
                 | JobParametersInvalidException e) {
            e.printStackTrace();

        }


    }

    /*@PostMapping
    public void importCsvtoDBJob(){
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("startAt", System.currentTimeMillis())
                .toJobParameters();

        try {
            jobLauncher.run(job,jobParameters);
        } catch (JobExecutionAlreadyRunningException
                 | JobRestartException
                 | JobInstanceAlreadyCompleteException
                 | JobParametersInvalidException e) {
            e.printStackTrace();
        }

    }*/

}
