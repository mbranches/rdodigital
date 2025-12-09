package com.branches.equipamento.repository;

import com.branches.equipamento.domain.EquipamentoDeRelatorioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipamentoDeRelatorioRepository extends JpaRepository<EquipamentoDeRelatorioEntity, Long> {
    List<EquipamentoDeRelatorioEntity> findAllByRelatorioId(Long relatorioId);

    Optional<EquipamentoDeRelatorioEntity> findByIdAndRelatorioId(Long id, Long relatorioId);

    boolean existsByRelatorioIdAndEquipamentoId(Long relatorioId, Long equipamentoId);
}