package com.promocodes.batchgeneration.util;

import com.opencsv.CSVWriter;
import com.promocodes.batchgeneration.draft.PromoCode;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;

import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

public class BatchUtilities {


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
