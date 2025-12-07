package com.branches.maodeobra.repository;

import com.branches.maodeobra.domain.MaoDeObraDeRelatorioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface MaoDeObraDeRelatorioRepository extends JpaRepository<MaoDeObraDeRelatorioEntity, Long> {
    List<MaoDeObraDeRelatorioEntity> findAllByRelatorioId(Long relatorioId);

    Optional<MaoDeObraDeRelatorioEntity> findByIdAndRelatorioId(Long id, Long relatorioId);

    List<MaoDeObraDeRelatorioEntity> findAllByRelatorioIdIn(Collection<Long> relatorioIds);
}