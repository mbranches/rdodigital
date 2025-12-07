package com.branches.material.repository;

import com.branches.material.domain.MaterialDeRelatorioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MaterialDeRelatorioRepository extends JpaRepository<MaterialDeRelatorioEntity, Long> {
    List<MaterialDeRelatorioEntity> findAllByRelatorioId(Long relatorioId);

    Optional<MaterialDeRelatorioEntity> findByIdAndRelatorioId(Long id, Long relatorioId);

    List<MaterialDeRelatorioEntity> findAllByRelatorioIdIn(Collection<Long> relatorioIds);
}