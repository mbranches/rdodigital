package com.branches.relatorio.repository;

import com.branches.relatorio.domain.MaoDeObraDeAtividadeDeRelatorioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface MaoDeObraDeAtividadeDeRelatorioRepository extends JpaRepository<MaoDeObraDeAtividadeDeRelatorioEntity, Long> {
    void removeAllByAtividadeDeRelatorioId(Long atividadeDeRelatorioId);

    List<MaoDeObraDeAtividadeDeRelatorioEntity> findAllByIdInAndAtividadeDeRelatorioId(Collection<Long> ids, Long atividadeDeRelatorioId);
}