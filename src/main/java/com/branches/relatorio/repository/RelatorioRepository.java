package com.branches.relatorio.repository;

import com.branches.relatorio.domain.RelatorioEntity;
import com.branches.relatorio.domain.enums.StatusRelatorio;
import com.branches.relatorio.repository.projections.RelatorioDetailsProjection;
import com.branches.relatorio.repository.projections.RelatorioProjection;
import com.branches.relatorio.repository.projections.RelatorioWithObraProjection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
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
        o.numeroContrato AS obraNumeroContrato,
        o.configuracaoRelatorios.recorrenciaRelatorio AS recorrenciaRelatorio,
        o.configuracaoRelatorios.modeloDeRelatorio.titulo AS tituloModeloDeRelatorio,
        o.configuracaoRelatorios.showAtividades AS showAtividades,
        o.configuracaoRelatorios.showCondicaoClimatica AS showCondicaoClimatica,
        o.configuracaoRelatorios.showComentarios AS showComentarios,
        o.configuracaoRelatorios.showEquipamentos AS showEquipamentos,
        o.configuracaoRelatorios.showMaoDeObra AS showMaoDeObra,
        o.configuracaoRelatorios.showOcorrencias AS showOcorrencias,
        o.configuracaoRelatorios.showMateriais AS showMateriais,
        o.configuracaoRelatorios.showHorarioDeTrabalho AS showHorarioDeTrabalho,
        r.dataInicio AS dataInicio,
        r.dataFim AS dataFim,
        r.numero AS numero,
        r.prazoContratualObra AS prazoContratual,
        r.prazoDecorridoObra AS prazoDecorrido,
        r.prazoPraVencerObra AS prazoPraVencer,
        r.caracteristicasManha AS caracteristicasManha,
        r.caracteristicasTarde AS caracteristicasTarde,
        r.caracteristicasNoite AS caracteristicasNoite,
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
    Optional<RelatorioDetailsProjection> findDetailsByIdExternoAndTenantId(String relatorioExternalId, Long tenantId);

    @Query("""
    SELECT r.idExterno AS idExterno,
        r.dataInicio AS dataInicio,
        r.dataFim AS dataFim,
        r.numero AS numero,
        r.status AS status,
        r.pdfUrl AS pdfUrl,
        o.idExterno AS obraIdExterno,
        o.nome AS obraNome,
        o.endereco AS obraEndereco,
        o.contratante AS obraContratante,
        o.responsavel AS obraResponsavel
    FROM RelatorioEntity r
        JOIN ObraEntity o ON o.id = r.obraId AND o.tenantId = r.tenantId
    WHERE r.obraId = :id
      AND r.ativo IS TRUE
    ORDER BY r.dataInicio DESC
    LIMIT 5
""")
    List<RelatorioProjection> findTop5ByObraIdProjection(Long id);

    @Query("""
    SELECT r.idExterno AS idExterno,
        r.dataInicio AS dataInicio,
        r.dataFim AS dataFim,
        r.numero AS numero,
        r.status AS status,
        r.pdfUrl AS pdfUrl,
        o.idExterno AS obraIdExterno,
        o.nome AS obraNome,
        o.endereco AS obraEndereco,
        o.contratante AS obraContratante,
        o.responsavel AS obraResponsavel,
        o.numeroContrato AS obraNumeroContrato
    FROM RelatorioEntity r
    JOIN ObraEntity o ON o.id = r.obraId AND o.tenantId = r.tenantId
    WHERE r.obraId = :obraId
        AND r.status = :statusRelatorio
        AND r.ativo IS TRUE
""")
    List<RelatorioProjection> findAllByObraIdAndStatusProjection(Long obraId, StatusRelatorio statusRelatorio, Pageable pageable);

    @Query("""
    SELECT r.idExterno AS idExterno,
        r.dataInicio AS dataInicio,
        r.dataFim AS dataFim,
        r.numero AS numero,
        r.status AS status,
        r.pdfUrl AS pdfUrl,
        o.idExterno AS obraIdExterno,
        o.nome AS obraNome,
        o.endereco AS obraEndereco,
        o.contratante AS obraContratante,
        o.responsavel AS obraResponsavel,
        o.numeroContrato AS obraNumeroContrato
    FROM RelatorioEntity r
    JOIN ObraEntity o ON o.id = r.obraId AND o.tenantId = r.tenantId
    WHERE r.obraId = :obraId
        AND r.ativo IS TRUE
""")
    List<RelatorioProjection> findAllByObraIdProjection(Long obraId, Pageable pageable);

    @Query("""
        SELECT r as relatorio,
               o as obra
        FROM RelatorioEntity r
            JOIN ObraEntity o ON o.id = r.obraId
        WHERE r.idExterno = :relatorioExternalId
          AND r.tenantId = :tenantId
    """)
    Optional<RelatorioWithObraProjection> findRelatorioWithObraByIdExternoAndTenantId(String relatorioExternalId, Long tenantId);

}