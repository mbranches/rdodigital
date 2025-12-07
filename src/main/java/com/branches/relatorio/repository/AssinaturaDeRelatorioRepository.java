package com.branches.relatorio.repository;

import com.branches.relatorio.domain.AssinaturaDeRelatorioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface AssinaturaDeRelatorioRepository extends JpaRepository<AssinaturaDeRelatorioEntity, Long> {
    List<AssinaturaDeRelatorioEntity> findAllByRelatorioId(Long relatorioId);

    List<AssinaturaDeRelatorioEntity> findAllByRelatorioIdIn(Collection<Long> relatorioIds);
}