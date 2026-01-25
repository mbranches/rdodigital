package com.branches.equipamento.repository;

import com.branches.equipamento.domain.EquipamentoEntity;
import com.branches.equipamento.repository.projections.ItemTopEquipamentosProjection;
import com.branches.equipamento.repository.projections.QuantidadeEquipamentoPorMesProjection;
import com.branches.equipamento.repository.projections.TotalDeEquipamentoPorMesProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipamentoRepository extends JpaRepository<EquipamentoEntity, Long> {
    Optional<EquipamentoEntity> findByIdAndTenantIdAndAtivoIsTrue(Long id, Long tenantId);

    List<EquipamentoEntity> findAllByTenantIdAndAtivoIsTrue(Long tenantId);

    @Query("""
    SELECT e.id AS id,
        e.descricao AS descricao,
        COALESCE(SUM(
            COALESCE(er.quantidade, 1)
        ), 0) AS quantidadeUso
    FROM EquipamentoEntity e
        JOIN EquipamentoDeRelatorioEntity er ON er.equipamento.id = e.id
        JOIN er.relatorio r
        JOIN ObraEntity o ON r.obraId = o.id
    WHERE r.ativo = true
        AND e.tenantId = :tenantId
        AND e.ativo = true
        AND o.ativo = true
        AND (:obraExternalId IS NULL OR o.idExterno = :obraExternalId)
    GROUP BY e.id, e.descricao
""")
    Page<ItemTopEquipamentosProjection> findTopEquipamentos(Long tenantId, String obraExternalId, Pageable pageRequest);

    @Query("""
    SELECT
        MONTH(r.dataInicio) AS mes,
        SUM(
            COALESCE(er.quantidade, 1)
        ) AS quantidade
    FROM EquipamentoDeRelatorioEntity er
        JOIN er.relatorio r
        JOIN ObraEntity o ON r.obraId = o.id
        JOIN er.equipamento e
    WHERE e.tenantId = :tenantId
      AND r.ativo = true
      AND e.ativo = true
      AND o.ativo = true
      AND (:equipamentoId IS NULL OR e.id = :equipamentoId)
      AND YEAR(r.dataInicio) = :ano
      AND (:obraExternalId IS NULL OR o.idExterno = :obraExternalId)
    GROUP BY MONTH(r.dataInicio)
    ORDER BY MONTH(r.dataInicio)
""")
    List<TotalDeEquipamentoPorMesProjection> findTotalEquipamentoPorMes(Long tenantId, Integer ano, String obraExternalId, Long equipamentoId);

    @Query("""
    SELECT
        MONTH(r.dataInicio) AS mes,
        e.id AS equipamentoId,
        e.descricao AS equipamentoDescricao,
        SUM(
            COALESCE(er.quantidade, 1)
        ) AS quantidade
    FROM EquipamentoDeRelatorioEntity er
    JOIN er.relatorio r
    JOIN ObraEntity o ON r.obraId = o.id
    JOIN er.equipamento e
    WHERE e.tenantId = :tenantId
        AND r.ativo = true
        AND e.ativo = true
        AND o.ativo = true
        AND YEAR(r.dataInicio) = :year
        AND (:obraExternalId IS NULL OR o.idExterno = :obraExternalId)
        GROUP BY MONTH(r.dataInicio), e.id, e.descricao
    """)
    List<QuantidadeEquipamentoPorMesProjection> findQuantidadeEquipamentoPorMes(Long tenantId, Integer year, String obraExternalId);

}
