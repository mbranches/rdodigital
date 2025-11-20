package com.branches.relatorio.rdo.repository;

import com.branches.relatorio.rdo.domain.CaracteristicaDePeriodoDoDiaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CaracteristicaDePeriodoDoDiaRepository extends JpaRepository<CaracteristicaDePeriodoDoDiaEntity, Long> {
    Optional<CaracteristicaDePeriodoDoDiaEntity> findByIdAndTenantId(Long id, Long tenantId);
}