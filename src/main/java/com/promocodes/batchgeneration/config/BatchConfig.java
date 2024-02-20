package com.promocodes.batchgeneration.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVWriter;
import com.promocodes.batchgeneration.draft.PromoCode;
import com.promocodes.batchgeneration.draft.PromoCodeRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

@Configuration
@RequiredArgsConstructor
public class BatchConfig {

    private final PromoCodeRepository repository;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    Logger log = Logger.getLogger("CONFIGURATION");
   /* @Bean
    public FlatFileItemReader<Draft> reader() {
        log.info("Reading from CSV");
        FlatFileItemReader<Draft> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(new FileSystemResource("src/main/resources/drafts.csv"));
        itemReader.setName("csvReader");
        itemReader.setLinesToSkip(1);
        itemReader.setLineMapper(lineMapper());
        return itemReader;

    }*/

    // JSON Reader
    @Bean
    public FlatFileItemReader<PromoCode> jsonReader(){

        final String jsonPath = "src/main/resources/draft.json";
        final String csvPath = "src/main/resources/drafts.csv";
        final ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode jsonNode = objectMapper.readTree(new File(jsonPath));
            System.out.println("totalCount " + jsonNode.get("draft").get("totalCount"));
            createPromoCodes(
                    jsonNode.get("draft").get("totalCount").intValue(),
                    jsonNode.get("draft").get("definitionTemplate").get("length").intValue(),
                    jsonNode.get("draft").get("definitionTemplate").get("delimitLength").intValue(),
                    jsonNode.get("draft").get("definitionTemplate").get("prefix").asText()
                    );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        FlatFileItemReader<PromoCode> jsonReader = new FlatFileItemReader<>();
        jsonReader.setResource(new FileSystemResource(csvPath));
        jsonReader.setName("jsonReader");
        jsonReader.setLinesToSkip(1);
        jsonReader.setLineMapper(lineMapper());

        return jsonReader;


    }

    @Bean
    public PromoCodeProcessor processor(){
        return new PromoCodeProcessor();
    }

    @Bean
    public RepositoryItemWriter<PromoCode> jsonWriter(){
        log.info("In method JSONWriter");
        RepositoryItemWriter<PromoCode> writer = new RepositoryItemWriter<>();
        writer.setRepository(repository);
        writer.setMethodName("save");
        return writer;

    }

    /*@Bean
    public DraftProcessor processor(){
        return new DraftProcessor();
    }*/

   /* @Bean
    public RepositoryItemWriter<Draft> write(){
        log.info("In method Write");
        RepositoryItemWriter<Draft> writer = new RepositoryItemWriter<>();
        writer.setRepository(repository);
        writer.setMethodName("save");
        return writer;

    }*/

    public Step jsonReadStep(){

        return new StepBuilder("jsonDraftsStep", jobRepository)
                .<PromoCode,PromoCode>chunk(10, platformTransactionManager)
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

    /*@Bean
    public Step importStep(){
        createPromoCodesCSV(10);
        return new StepBuilder("importDrafts", jobRepository)
                .<Draft,Draft>chunk(10, platformTransactionManager)
                .allowStartIfComplete(true)
                .reader(reader())
                .processor(processor())
                .writer(write())
                .build();
    }

    /*@Bean
    public Job runJob(){
        return new JobBuilder("exportDrafts", jobRepository)
                .start(importStep())
                .build();

    }*/

    private LineMapper<PromoCode> lineMapper(){
        DefaultLineMapper<PromoCode> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("id", "name");

        BeanWrapperFieldSetMapper<PromoCode> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(PromoCode.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        return lineMapper;
    }

    //JSON Line Mapper


    private boolean createPromoCodes(int totalCount, int length, int delimit, String prefix){

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

    public String randomPromoCode(int length, int delimit){
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
