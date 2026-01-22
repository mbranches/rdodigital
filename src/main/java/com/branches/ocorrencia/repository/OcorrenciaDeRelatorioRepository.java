package com.branches.ocorrencia.repository;

import com.branches.ocorrencia.domain.OcorrenciaDeRelatorioEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OcorrenciaDeRelatorioRepository extends JpaRepository<OcorrenciaDeRelatorioEntity, Long> {
    List<OcorrenciaDeRelatorioEntity> findAllByRelatorioId(Long relatorioId);

    Optional<OcorrenciaDeRelatorioEntity> findByIdAndRelatorioId(Long id, Long relatorioId);

    @Query("""
        SELECT o
        FROM OcorrenciaDeRelatorioEntity o
        WHERE o.tenantId = :tenantId
        AND o.relatorio.obraId = :obraId
        AND (:canViewOnlyAprovados = false OR o.relatorio.status = 'APROVADO')
""")
    Page<OcorrenciaDeRelatorioEntity> findAllByObraIdAndTenantId(Long obraId, Long tenantId, Boolean canViewOnlyAprovados, Pageable pageRequest);

}