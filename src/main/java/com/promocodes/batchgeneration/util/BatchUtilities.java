package com.promocodes.batchgeneration.util;

import com.kafka.broker.payload.Draft;
import com.opencsv.CSVWriter;
import com.promocodes.batchgeneration.BatchGenerationApplication;
import com.promocodes.batchgeneration.draftdb.PromoCodeEntity;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

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

    public static void createEmptyJsonDraft(){

      /*  try{

            File draftJson = new File("src/main/resources/draft.json");
            if (!draftJson.createNewFile()) {
                System.out.println("File already exists");
            }else{
                FileWriter fileWriter = new FileWriter(draftJson);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.write("{\n" +
                        "  \"draft\": {\n" +
                        "    \"maxRedemptionLimit\": 0,\n" +
                        "    \"totalCount\": 0,\n" +
                        "    \"campaignDescription\": \"\",\n" +
                        "    \"thirdPartyDetail\": \"\",\n" +
                        "    \"promotionCodeType\": \"\",\n" +
                        "    \"definitionTemplate\": {\n" +
                        "      \"length\": 1 ,\n" +
                        "      \"delimitLength\": 1,\n" +
                        "      \"prefix\": \"\"\n" +
                        "    },\n" +
                        "    \"offerId\": \"\"\n" +
                        "  },\n" +
                        "  \"versionedBy\": \"\"\n" +
                        "}");
                bufferedWriter.newLine();
                bufferedWriter.close();
            }



        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/

    }


    public static void createJsonDraft(Draft draft) {
       try{
            FileWriter fileWriter = new FileWriter("src/main/resources/draft.json");
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write("{\n" +
                    "  \"draft\": {\n" +
                    "    \"maxRedemptionLimit\":"+draft.getMaxRedemptionLimit()+",\n" +
                    "    \"totalCount\":"+draft.getTotalCount()+",\n" +
                    "    \"campaignDescription\":\""+draft.getCampaignDescription()+"\",\n" +
                    "    \"thirdPartyDetail\": \""+draft.getThirdPartyDetail()+"\",\n" +
                    "    \"promotionCodeType\": \""+draft.getPromotionCodeType()+"\",\n" +
                    "    \"definitionTemplate\": {\n" +
                    "      \"length\": "+draft.getDefinitionTemplate().getLength()+",\n" +
                    "      \"delimitLength\": "+draft.getDefinitionTemplate().getDelimitLength()+",\n" +
                    "      \"prefix\": \""+draft.getDefinitionTemplate().getPrefix()+"\"\n" +
                    "    },\n" +
                    "    \"offerId\": \""+draft.getOfferId()+"\"\n" +
                    "  },\n" +
                    "  \"versionedBy\": \"\"\n" +
                    "}");
            bufferedWriter.newLine();
            bufferedWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static LineMapper<PromoCodeEntity> lineMapper(){
        DefaultLineMapper<PromoCodeEntity> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("name");

        BeanWrapperFieldSetMapper<PromoCodeEntity> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(PromoCodeEntity.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        return lineMapper;
    }

    public static boolean createPromoCodes(int totalCount, int length, int delimit, String prefix){

        String filePath = "src/main/resources/promocodes.csv";
        System.out.println("    * CREATING PROMO CODES");
        try(CSVWriter csvWriter = new CSVWriter(
                new FileWriter(filePath))) {
            String[] promoCodes = new String[totalCount];
            String[] header = {"PromoCodeTag","promo-code"};
            csvWriter.writeNext(header);
            int id = 1;
            for(int i=0; i< promoCodes.length; i++){
                promoCodes[i] = prefix+randomPromoCode(length,delimit);
                String[] row = {promoCodes[i]};
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
