package com.promocodes.batchgeneration.draft;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class DefinitionTemplate {
    @Id
    @GeneratedValue
    private int id;
    private int length;
    private int delimitLength;
    private String prefix;
}
