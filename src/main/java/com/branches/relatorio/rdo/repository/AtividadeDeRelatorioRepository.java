package com.branches.relatorio.rdo.repository;

import com.branches.relatorio.rdo.domain.AtividadeDeRelatorioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface AtividadeDeRelatorioRepository extends JpaRepository<AtividadeDeRelatorioEntity, Long> {
    List<AtividadeDeRelatorioEntity> findAllByRelatorioId(Long relatorioId);

    void removeAllByRelatorioId(Long relatorioId);

    List<AtividadeDeRelatorioEntity> findAllByIdInAndRelatorioId(Collection<Long> ids, Long relatorioId);

    void removeAllByRelatorioIdAndIdNotIn(Long relatorioId, Collection<Long> ids);
}