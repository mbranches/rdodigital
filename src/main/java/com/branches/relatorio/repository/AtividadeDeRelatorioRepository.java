package com.branches.relatorio.repository;

import com.branches.relatorio.domain.AtividadeDeRelatorioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface AtividadeDeRelatorioRepository extends JpaRepository<AtividadeDeRelatorioEntity, Long> {
    List<AtividadeDeRelatorioEntity> findAllByRelatorioId(Long relatorioId);

    void removeAllByRelatorioId(Long relatorioId);

    List<AtividadeDeRelatorioEntity> findAllByIdInAndRelatorioId(Collection<Long> ids, Long relatorioId);

    void removeAllByRelatorioIdAndIdNotIn(Long relatorioId, Collection<Long> ids);

    Optional<AtividadeDeRelatorioEntity> findByIdAndRelatorioId(Long id, Long relatorioId);
}