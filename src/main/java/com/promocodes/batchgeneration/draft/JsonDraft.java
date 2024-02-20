package com.promocodes.batchgeneration.draft;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Entity
public class JsonDraft {
        @Id
        @GeneratedValue
        private int id;
        private int maxRedemptionLimit;
        private int totalCount;
        private String campaignDescription;
        private String thirdPartyDetail;
        private String promotionCodeType;
        @JdbcTypeCode(SqlTypes.JSON)
        private DefinitionTemplate definitionTemplate;

        private String offerId;

}
