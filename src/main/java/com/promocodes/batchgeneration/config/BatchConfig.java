package com.promocodes.batchgeneration.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.promocodes.batchgeneration.draftdb.PromoCodeEntity;
import com.promocodes.batchgeneration.draftdb.PromoCodeRepository;
import com.promocodes.batchgeneration.kafka.KafkaUserListener;
import com.promocodes.batchgeneration.util.BatchUtilities;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.xml.ExceptionElementParser;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.File;
import java.io.IOException;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfig {

    @Getter
    KafkaUserListener kafkaUserListener = new KafkaUserListener();

    private final PromoCodeRepository repository;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;



    Logger LOGGER = LoggerFactory.getLogger(BatchConfig.class);

    // JSON Reader
    @Bean
    public FlatFileItemReader<PromoCodeEntity> jsonReader(){

        /*final String jsonPath = "src/main/resources/draft.json";
        final String csvPath = "src/main/resources/promocodes.csv";
        final ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode jsonNode = objectMapper.readTree(new File(jsonPath));
            System.out.println("totalCount " + jsonNode.get("draft").get("totalCount"));
            BatchUtilities.createPromoCodes(
                    jsonNode.get("draft").get("totalCount").intValue(),
                    jsonNode.get("draft").get("definitionTemplate").get("length").intValue(),
                    jsonNode.get("draft").get("definitionTemplate").get("delimitLength").intValue(),
                    jsonNode.get("draft").get("definitionTemplate").get("prefix").asText()
                    );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Create CSV to dump promo codes generated.
        FlatFileItemReader<PromoCodeEntity> jsonReader = new FlatFileItemReader<>();
        jsonReader.setResource(new FileSystemResource(csvPath));
        jsonReader.setName("jsonReader");
        jsonReader.setLinesToSkip(1);
        jsonReader.setLineMapper(BatchUtilities.lineMapper());

        return jsonReader;*/
        return null;

    }

    @Bean
    public PromoCodeProcessor processor(){
        ;return new PromoCodeProcessor();
    }


    @Bean
    public RepositoryItemWriter<PromoCodeEntity> jsonWriter(){
        RepositoryItemWriter<PromoCodeEntity> writer = new RepositoryItemWriter<>();
        writer.setRepository(repository);
        writer.setMethodName("save");
        return writer;

    }

    @Bean
    public Step readJsonDraftGeneratePromoCodes(){

        return new StepBuilder("readJsonDraftGeneratePromoCodes", jobRepository)
                .allowStartIfComplete(true)
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

                        final String jsonPath = "src/main/resources/draft.json";
                        final ObjectMapper objectMapper = new ObjectMapper();
                        System.out.println("STEP 1:");
                        try {
                            System.out.println("    * READING JSON");
                            JsonNode jsonNode = objectMapper.readTree(new File(jsonPath));
                            System.out.println("    * Total PromoCodes to be created:  " + jsonNode.get("draft").get("totalCount"));
                            BatchUtilities.createPromoCodes(
                                    jsonNode.get("draft").get("totalCount").intValue(),
                                    jsonNode.get("draft").get("definitionTemplate").get("length").intValue(),
                                    jsonNode.get("draft").get("definitionTemplate").get("delimitLength").intValue(),
                                    jsonNode.get("draft").get("definitionTemplate").get("prefix").asText()
                            );
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        System.out.println("    * PROMO CODES CREATED");
                        return RepeatStatus.FINISHED;
                    }
                }, platformTransactionManager).build();
    }

    @Bean
    public Step sendPromoCodes(){
        return new StepBuilder("sendPromoCodes", jobRepository)
                .allowStartIfComplete(true)
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("STEP 2:");
                        boolean execSuccess = false;
                        if(execSuccess){
                            throw new Exception("Throwing Exception for FAIL testing");
                        }
                        System.out.println("Implement behavior to send promocodes created to endpoint");
                        return RepeatStatus.FINISHED;
                    }
                }, platformTransactionManager).build();
    }

   /* @Bean
    public Step createPromoCodesStep(){
        System.out.println("Create Promo Codes Method");
        return new StepBuilder("createPromoCodesStep", jobRepository)
                .<PromoCodeEntity, PromoCodeEntity>chunk(10, platformTransactionManager)
                .reader(jsonReader())
                .processor(processor())
                .writer(jsonWriter())
                .allowStartIfComplete(true)
                .build();
    }*/

    @Bean
    public Job runJob(){
        return new JobBuilder("create-promo-codes", jobRepository)
                //.preventRestart()
                .start(readJsonDraftGeneratePromoCodes())
                .next(sendPromoCodes())
                .build();

    }

  }
