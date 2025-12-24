package com.branches.relatorio.repository;

import com.branches.obra.domain.LogoDeRelatorioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LogoDeRelatorioRepository extends JpaRepository<LogoDeRelatorioEntity, Long> {
    List<LogoDeRelatorioEntity> findAllByTenantIdAndIsLogoDoTenantIsTrue(Long tenantId);
}