package com.promocodes.batchgeneration.draftdb;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
public class PromoCodeEntity {

    @Id
    @GeneratedValue
    private Integer id;
    private String name;

}
