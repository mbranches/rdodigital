package com.branches.relatorio.repository;

import com.branches.relatorio.domain.MaoDeObraDeRelatorioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface MaoDeObraDeRelatorioRepository extends JpaRepository<MaoDeObraDeRelatorioEntity, Long> {
    List<MaoDeObraDeRelatorioEntity> findAllByRelatorioId(Long relatorioId);

    List<MaoDeObraDeRelatorioEntity> findAllByIdInAndRelatorioId(Collection<Long> ids, Long relatorioId);

    void removeAllByRelatorioId(Long relatorioId);

    Optional<MaoDeObraDeRelatorioEntity> findByIdAndRelatorioId(Long id, Long relatorioId);
}