package com.promocodes.batchgeneration.draft;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
public class PromoCode {

    @Id
    @GeneratedValue
    private Integer id;
    private String name;

}
