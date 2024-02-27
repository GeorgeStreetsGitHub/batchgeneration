package com.promocodes.batchgeneration.draftdb;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PromoCodeRepository extends JpaRepository<PromoCodeEntity, Integer> {
}
