package com.promocodes.batchgeneration.config;

import com.promocodes.batchgeneration.draft.Draft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class DraftProcessor implements ItemProcessor<Draft, Draft> {
    Logger log = LoggerFactory.getLogger("PROCESSOR");

    @Override
    public Draft process(Draft draft) throws Exception {
        return draft;
    }


}
