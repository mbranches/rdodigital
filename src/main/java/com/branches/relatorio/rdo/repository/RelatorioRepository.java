package com.branches.relatorio.rdo.repository;

import com.branches.relatorio.rdo.domain.RelatorioEntity;
import com.branches.relatorio.rdo.repository.projections.RelatorioDetailsProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RelatorioRepository extends JpaRepository<RelatorioEntity, Long> {
    Optional<RelatorioEntity> findFirstByTenantIdAndObraIdAndAtivoIsTrueOrderByEnversCreatedDateDesc(Long tenantId, Long obraId);

    long countByTenantIdAndObraIdAndAtivoIsTrue(Long tenantId, Long obraId);

    Optional<RelatorioEntity> findByIdExternoAndTenantIdAndAtivoIsTrue(String relatorioExternalId, Long tenantId);

    @Query("""
    SELECT r.id AS id,
        r.idExterno AS idExterno,
        t.logoUrl AS tenantLogoUrl,
        o.idExterno AS obraIdExterno,
        o.nome AS obraNome,
        o.endereco AS obraEndereco,
        o.contratante AS obraContratante,
        o.responsavel AS obraResponsavel,
        r.data AS data,
        r.numero AS numero,
        r.prazoContratualObra AS prazoContratual,
        r.prazoDecorridoObra AS prazoDecorrido,
        r.prazoPraVencerObra AS prazoPraVencer,
        CASE
            WHEN :canViewCondicaoDoClima = true THEN r.caracteristicasManha
            ELSE NULL
        END AS caracteristicaManha,
        CASE
            WHEN :canViewCondicaoDoClima = true THEN r.caracteristicasTarde
            ELSE NULL
        END AS caracteristicaTarde,
        CASE
            WHEN :canViewCondicaoDoClima = true THEN r.caracteristicasNoite
            ELSE NULL
        END AS caracteristicaNoite,
        CASE
            WHEN :canViewCondicaoDoClima = true THEN r.indiciePluviometrico
            ELSE NULL
        END AS indiciePluviometrico,
        r.indiciePluviometrico AS indicePluviometrico,
        r.status AS status,
        u.nome AS criadoPor,
        r.enversCreatedDate AS criadoEm,
        u2.nome AS ultimaModificacaoPor,
        r.enversLastModifiedDate AS ultimaModificacaoEm
    FROM RelatorioEntity r
        JOIN TenantEntity t ON r.tenantId = t.id
        JOIN ObraEntity o ON o.id = r.obraId AND o.tenantId = r.tenantId
        JOIN UserEntity u ON u.id = r.enversCreator
        JOIN UserEntity u2 ON u2.id = r.enversModifier
    WHERE r.idExterno = :relatorioExternalId
      AND r.tenantId = :tenantId
      AND t.ativo IS TRUE
""")
    RelatorioDetailsProjection findDetailsByIdExternoAndTenantId(String relatorioExternalId, Long tenantId, Boolean canViewCondicaoDoClima);
}