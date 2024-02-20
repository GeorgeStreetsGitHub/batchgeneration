package com.promocodes.batchgeneration.config;

import com.promocodes.batchgeneration.draft.PromoCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class DraftProcessor implements ItemProcessor<PromoCode, PromoCode> {
    Logger log = LoggerFactory.getLogger("PROCESSOR");

    @Override
    public PromoCode process(PromoCode draft) throws Exception {
        return draft;
    }


}
