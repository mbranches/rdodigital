package com.branches.relatorio.repository;

import com.branches.material.domain.MaterialDeRelatorioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface MaterialDeRelatorioRepository extends JpaRepository<MaterialDeRelatorioEntity, Long> {
    void removeAllByRelatorioId(Long relatorioId);

    void removeAllByIdNotInAndRelatorioId(Collection<Long> ids, Long relatorioId);

    List<MaterialDeRelatorioEntity> findAllByIdInAndRelatorioId(Collection<Long> ids, Long relatorioId);

    List<MaterialDeRelatorioEntity> findAllByRelatorioId(Long relatorioId);
}