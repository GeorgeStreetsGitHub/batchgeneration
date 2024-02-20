package com.promocodes.batchgeneration.config;

import com.promocodes.batchgeneration.draft.JsonDraft;
import com.promocodes.batchgeneration.draft.PromoCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class PromoCodeProcessor implements ItemProcessor<PromoCode, PromoCode> {
    Logger log = LoggerFactory.getLogger("JSON PROCESSOR");

    @Override
    public PromoCode process(PromoCode promoCode) throws Exception {
        return promoCode;
    }
}