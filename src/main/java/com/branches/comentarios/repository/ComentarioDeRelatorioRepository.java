package com.branches.comentarios.repository;

import com.branches.comentarios.model.ComentarioDeRelatorioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ComentarioDeRelatorioRepository extends JpaRepository<ComentarioDeRelatorioEntity, Long> {
    List<ComentarioDeRelatorioEntity> findAllByRelatorioId(Long relatorioId);

    Optional<ComentarioDeRelatorioEntity> findByIdAndRelatorioId(Long id, Long relatorioId);

    List<ComentarioDeRelatorioEntity> findAllByRelatorioIdIn(Collection<Long> relatorioIds);
}