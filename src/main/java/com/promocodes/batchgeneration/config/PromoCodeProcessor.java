package com.promocodes.batchgeneration.config;

import com.promocodes.batchgeneration.draftdb.PromoCodeEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class PromoCodeProcessor implements ItemProcessor<PromoCodeEntity, PromoCodeEntity> {
    Logger log = LoggerFactory.getLogger("JSON PROCESSOR");

    @Override
    public PromoCodeEntity process(PromoCodeEntity promoCodeEntity) throws Exception {
        return promoCodeEntity;
    }
}