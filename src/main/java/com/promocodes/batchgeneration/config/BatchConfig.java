package com.promocodes.batchgeneration.config;

import com.opencsv.CSVWriter;
import com.promocodes.batchgeneration.draft.Draft;
import com.promocodes.batchgeneration.draft.DraftRepository;
import lombok.RequiredArgsConstructor;
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

import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Logger;

@Configuration
@RequiredArgsConstructor
public class BatchConfig {


    private final DraftRepository repository;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    Logger log = Logger.getLogger("CONFIGURATION");
    @Bean
    public FlatFileItemReader<Draft> reader() {
        log.info("Reading from CSV");
        FlatFileItemReader<Draft> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(new FileSystemResource("src/main/resources/drafts.csv"));
        itemReader.setName("csvReader");
        itemReader.setLinesToSkip(1);
        itemReader.setLineMapper(lineMapper());
        return itemReader;

    }

    @Bean
    public DraftProcessor processor(){
        return new DraftProcessor();
    }

    @Bean
    public RepositoryItemWriter<Draft> write(){
        log.info("In method Write");
        RepositoryItemWriter<Draft> writer = new RepositoryItemWriter<>();
        writer.setRepository(repository);
        writer.setMethodName("save");
        return writer;

    }

    @Bean
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

    @Bean
    public Job runJob(){
        return new JobBuilder("exportDrafts", jobRepository)
                .start(importStep())
                .build();

    }

    private LineMapper<Draft> lineMapper(){
        DefaultLineMapper<Draft> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("id", "name");

        BeanWrapperFieldSetMapper<Draft> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Draft.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        return lineMapper;
    }

    private boolean createPromoCodesCSV(int threshold){
        String filePath = "src/main/resources/drafts.csv";
        String[] UUIDs = new String[threshold];

        try(CSVWriter csvWriter = new CSVWriter(new FileWriter(filePath))) {
            String[] header = {"id", "name"};
            csvWriter.writeNext(header);
            int id=1;
            for(int i=0; i<threshold; i++){
                UUIDs[i]= String.valueOf(UUID.randomUUID());
                String[] row = {String.valueOf(id), UUIDs[i]};
                csvWriter.writeNext(row);
                id+=1;
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }
}
