package com.branches.material.repository;

import com.branches.material.domain.MaterialEntity;
import com.branches.material.repository.projections.ItemTopMateriaisProjection;
import com.branches.material.repository.projections.QuantidadeMaterialPorMesProjection;
import com.branches.material.repository.projections.TotalDeMaterialPorMesProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaterialRepository extends JpaRepository<MaterialEntity, Long> {
    Optional<MaterialEntity> findByIdAndTenantIdAndAtivoIsTrue(Long id, Long tenantId);

    List<MaterialEntity> findAllByTenantIdAndAtivoIsTrue(Long tenantId);

    @Query("""
    SELECT e.id AS id,
        e.descricao AS descricao,
        COUNT(1) AS quantidadeUso
    FROM MaterialEntity e
        JOIN MaterialDeRelatorioEntity m ON m.material.id = e.id
        JOIN m.relatorio r
        JOIN ObraEntity o ON r.obraId = o.id
    WHERE r.ativo = true
        AND e.tenantId = :tenantId
        AND e.ativo = true
        AND o.ativo = true
        AND (:obraExternalId IS NULL OR o.idExterno = :obraExternalId)
    GROUP BY e.id, e.descricao
""")
    Page<ItemTopMateriaisProjection> findTopMateriais(Long tenantId, String obraExternalId, Pageable pageable);

    @Query("""
    SELECT
        MONTH(r.dataInicio) AS mes,
        COUNT(1) AS quantidade
    FROM MaterialDeRelatorioEntity mr
        JOIN mr.relatorio r
        JOIN ObraEntity o ON r.obraId = o.id
        JOIN mr.material m
    WHERE m.tenantId = :tenantId
      AND r.ativo = true
      AND m.ativo = true
      AND o.ativo = true
      AND (:materialId IS NULL OR m.id = :materialId)
      AND YEAR(r.dataInicio) = :ano
      AND (:obraExternalId IS NULL OR o.idExterno = :obraExternalId)
    GROUP BY MONTH(r.dataInicio)
    ORDER BY MONTH(r.dataInicio)
""")
    List<TotalDeMaterialPorMesProjection> findTotalMaterialPorMes(Long tenantId, Integer ano, String obraExternalId, Long materialId);

    @Query("""
    SELECT
        MONTH(r.dataInicio) AS mes,
        m.id AS materialId,
        m.descricao AS materialDescricao,
        COUNT(1) AS quantidade
    FROM MaterialDeRelatorioEntity mr
    JOIN mr.relatorio r
    JOIN ObraEntity o ON r.obraId = o.id
    JOIN mr.material m
    WHERE m.tenantId = :tenantId
        AND r.ativo = true
        AND m.ativo = true
        AND o.ativo = true
        AND YEAR(r.dataInicio) = :ano
        AND (:obraExternalId IS NULL OR o.idExterno = :obraExternalId)
        GROUP BY MONTH(r.dataInicio), m.id, m.descricao
""")
    List<QuantidadeMaterialPorMesProjection> findQuantidadeMaterialPorMes(Long tenantId, Integer ano, String obraExternalId);
}

