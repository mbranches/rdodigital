package com.branches.relatorio.repository;

import com.branches.relatorio.domain.ComentarioDeRelatorioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ComentarioDeRelatorioRepository extends JpaRepository<ComentarioDeRelatorioEntity, Long> {
    List<ComentarioDeRelatorioEntity> findAllByRelatorioId(Long relatorioId);

    void removeAllByRelatorioId(Long relatorioId);

    List<ComentarioDeRelatorioEntity> findAllByIdInAndRelatorioId(Collection<Long> ids, Long relatorioId);

    void removeAllByIdNotInAndRelatorioId(Collection<Long> ids, Long relatorioId);
}