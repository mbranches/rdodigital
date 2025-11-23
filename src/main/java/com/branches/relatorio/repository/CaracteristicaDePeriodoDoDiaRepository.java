package com.branches.relatorio.repository;

import com.branches.relatorio.domain.CaracteristicaDePeriodoDoDiaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CaracteristicaDePeriodoDoDiaRepository extends JpaRepository<CaracteristicaDePeriodoDoDiaEntity, Long> {
    Optional<CaracteristicaDePeriodoDoDiaEntity> findByIdAndTenantId(Long id, Long tenantId);
}