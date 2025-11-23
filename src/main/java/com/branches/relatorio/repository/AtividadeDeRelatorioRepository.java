package com.branches.relatorio.repository;

import com.branches.atividade.domain.AtividadeDeRelatorioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AtividadeDeRelatorioRepository extends JpaRepository<AtividadeDeRelatorioEntity, Long> {
    List<AtividadeDeRelatorioEntity> findAllByRelatorioId(Long relatorioId);

    Optional<AtividadeDeRelatorioEntity> findByIdAndRelatorioId(Long id, Long relatorioId);
}