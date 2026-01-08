package com.branches.plano.repository;

import com.branches.plano.domain.PeriodoTesteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PeriodoTesteRepository extends JpaRepository<PeriodoTesteEntity, Long> {
    boolean existsByTenantId(Long tenantId);

    Optional<PeriodoTesteEntity> findByTenantId(Long tenantId);
}