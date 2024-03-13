package com.kafka.broker.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DefinitionTemplate {

    private int id;
    private int length;

    private int delimitLength;
    private String prefix;

    @Override
    public String toString() {
        return "DefinitionTemplate{" +
                "id=" + id +
                ", length=" + length +
                ", delimitLength=" + delimitLength +
                ", prefix='" + prefix + '\'' +
                '}';
    }


}
