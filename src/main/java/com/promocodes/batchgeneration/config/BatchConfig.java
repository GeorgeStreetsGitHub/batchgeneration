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
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.File;
import java.io.IOException;

@Configuration
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


        final String jsonPath = "src/main/resources/draft.json";
        final String csvPath = "src/main/resources/drafts.csv";
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

        FlatFileItemReader<PromoCodeEntity> jsonReader = new FlatFileItemReader<>();
        jsonReader.setResource(new FileSystemResource(csvPath));
        jsonReader.setName("jsonReader");
        jsonReader.setLinesToSkip(1);
        jsonReader.setLineMapper(BatchUtilities.lineMapper());

        return jsonReader;

    }

    @Bean
    public PromoCodeProcessor processor(){
        return new PromoCodeProcessor();
    }

    @Bean
    public RepositoryItemWriter<PromoCodeEntity> jsonWriter(){
        LOGGER.info("In method JSONWriter");
        RepositoryItemWriter<PromoCodeEntity> writer = new RepositoryItemWriter<>();
        writer.setRepository(repository);
        writer.setMethodName("save");
        return writer;

    }

    public Step jsonReadStep(){

        return new StepBuilder("jsonDraftsStep", jobRepository)
                .<PromoCodeEntity, PromoCodeEntity>chunk(10, platformTransactionManager)
                .allowStartIfComplete(true)
                .reader(jsonReader())
                .processor(processor())
                .writer(jsonWriter())
                .build();
    }

    @Bean
    public Job runJob(){
        return new JobBuilder("exportDrafts", jobRepository)
                .start(jsonReadStep())
                .build();

    }

  }
