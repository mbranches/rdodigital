package com.branches.relatorio.repository;

import com.branches.relatorio.domain.ArquivoDeRelatorioDeUsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ArquivoDeRelatorioDeUsuarioRepository extends JpaRepository<ArquivoDeRelatorioDeUsuarioEntity, Long> {
    List<ArquivoDeRelatorioDeUsuarioEntity> findAllByRelatorioIdAndUserIdIn(Long relatorioId, Collection<Long> userIds);

    Optional<ArquivoDeRelatorioDeUsuarioEntity> findByUserIdAndRelatorioId(Long userId, Long relatorioId);

    List<ArquivoDeRelatorioDeUsuarioEntity> findAllByRelatorioIdInAndUserIdIn(Collection<Long> relatorioIds, Collection<Long> userIds);
}