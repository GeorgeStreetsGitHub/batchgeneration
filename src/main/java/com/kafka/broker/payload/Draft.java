package com.kafka.broker.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Draft {

    private int maxRedemptionLimit;
    private int totalCount;

    private String campaignDescription;
    private String thirdPartyDetail;
    private String promotionCodeType;
    private DefinitionTemplate definitionTemplate;

    private String offerId;

    @Override
    public String toString() {
        return "draft: {" +
                "  maxRedemptionLimit=" + maxRedemptionLimit +
                ", totalCount=" + totalCount +
                ", campaignDescription='" + campaignDescription + '\'' +
                ", thirdPartyDetail='" + thirdPartyDetail + '\'' +
                ", promotionCodeType='" + promotionCodeType + '\'' +
                ", definitionTemplate=" + definitionTemplate +
                ", offerId='" + offerId + '\'' +
                '}';
    }

}
