package com.branches.relatorio.repository;

import com.branches.relatorio.domain.RelatorioEntity;
import com.branches.relatorio.domain.enums.StatusRelatorio;
import com.branches.relatorio.repository.projections.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RelatorioRepository extends JpaRepository<RelatorioEntity, Long> {
    Optional<RelatorioEntity> findFirstByTenantIdAndObraIdAndAtivoIsTrueAndIdIsNotOrderByEnversCreatedDateDesc(Long tenantId, Long obraId, Long id);

    long countByTenantIdAndObraIdAndAtivoIsTrue(Long tenantId, Long obraId);

    Optional<RelatorioEntity> findByIdExternoAndTenantIdAndAtivoIsTrue(String relatorioExternalId, Long tenantId);

    @Query("""
    SELECT r.id AS id,
        r.idExterno AS idExterno,
        t.id AS tenantId,
        t.idExterno AS tenantIdExterno,
        o.id AS obraId,
        o.idExterno AS obraIdExterno,
        o.nome AS obraNome,
        o.endereco AS obraEndereco,
        o.contratante AS obraContratante,
        o.responsavel AS obraResponsavel,
        o.numeroContrato AS obraNumeroContrato,
        lr1 AS logoDeRelatorio1,
        lr2 AS logoDeRelatorio2,
        lr3 AS logoDeRelatorio3,
        cr.recorrenciaRelatorio AS recorrenciaRelatorio,
        cr.modeloDeRelatorio.titulo AS tituloModeloDeRelatorio,
        cr.showAtividades AS showAtividades,
        cr.showCondicaoClimatica AS showCondicaoClimatica,
        cr.showComentarios AS showComentarios,
        cr.showEquipamentos AS showEquipamentos,
        cr.showMaoDeObra AS showMaoDeObra,
        cr.showOcorrencias AS showOcorrencias,
        cr.showMateriais AS showMateriais,
        cr.showHorarioDeTrabalho AS showHorarioDeTrabalho,
        cr.showFotos AS showFotos,
        cr.showVideos AS showVideos,
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
        r.horaInicioTrabalhos AS horaInicioTrabalhos,
        r.horaFimTrabalhos AS horaFimTrabalhos,
        r.minutosIntervalo AS minutosIntervalo,
        r.minutosTrabalhados AS minutosTrabalhados,
        r.status AS status,
        r.tipoMaoDeObra AS tipoMaoDeObra,
        u.nome AS criadoPor,
        r.enversCreatedDate AS criadoEm,
        u2.nome AS ultimaModificacaoPor,
        r.enversLastModifiedDate AS ultimaModificacaoEm
    FROM RelatorioEntity r
        JOIN TenantEntity t ON r.tenantId = t.id
        JOIN ObraEntity o ON o.id = r.obraId AND o.tenantId = r.tenantId
        JOIN o.configuracaoRelatorios cr
        LEFT JOIN cr.logoDeRelatorio1 lr1
        LEFT JOIN cr.logoDeRelatorio2 lr2
        LEFT JOIN cr.logoDeRelatorio3 lr3
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
        o.idExterno AS obraIdExterno,
        o.nome AS obraNome,
        o.endereco AS obraEndereco,
        o.contratante AS obraContratante,
        o.responsavel AS obraResponsavel,
        (
            SELECT COUNT(1)
            FROM ArquivoEntity a
            WHERE a.tipoArquivo = 'FOTO'
                AND a.relatorio.id = r.id
        ) AS quantidadeFotos
    FROM RelatorioEntity r
        JOIN ObraEntity o ON o.id = r.obraId AND o.tenantId = r.tenantId
    WHERE r.obraId = :id
      AND r.ativo IS TRUE
    ORDER BY r.dataInicio DESC, r.enversCreatedDate DESC
    LIMIT 5
""")
    List<RelatorioProjection> findTop5ByObraIdProjection(Long id);

    @Query("""
        SELECT r as relatorio,
               o as obra
        FROM RelatorioEntity r
            JOIN ObraEntity o ON o.id = r.obraId
        WHERE r.idExterno = :relatorioExternalId
          AND r.tenantId = :tenantId
          AND r.ativo IS TRUE
    """)
    Optional<RelatorioWithObraProjection> findRelatorioWithObraByIdExternoAndTenantId(String relatorioExternalId, Long tenantId);

    List<RelatorioEntity> findAllByObraId(Long obraId);

    @Query("""
    SELECT r.idExterno AS idExterno,
        r.dataInicio AS dataInicio,
        r.dataFim AS dataFim,
        r.numero AS numero,
        r.status AS status,
        o.idExterno AS obraIdExterno,
        o.nome AS obraNome,
        o.endereco AS obraEndereco,
        o.contratante AS obraContratante,
        o.responsavel AS obraResponsavel,
        o.numeroContrato AS obraNumeroContrato,
        (
            SELECT COUNT(1)
            FROM ArquivoEntity a
            WHERE a.tipoArquivo = 'FOTO'
                AND a.relatorio.id = r.id
        ) AS quantidadeFotos
    FROM RelatorioEntity r
    JOIN ObraEntity o ON o.id = r.obraId AND o.tenantId = r.tenantId
    WHERE r.tenantId = :tenantId
        AND (:perfilIsAdministrador = true OR o.id IN :obrasIdAllowed)
        AND r.ativo IS TRUE
        AND (:canViewOnlyAprovados = false OR r.status = 'APROVADO')
        AND (:status IS NULL OR r.status = :status)
        AND (:obraExternalId IS NULL OR o.idExterno = :obraExternalId)
        AND (:numero IS NULL OR CAST(r.numero AS string) LIKE CONCAT(CAST(:numero AS string), '%'))
        AND (CAST(:dataInicio AS string) IS NULL OR r.dataInicio = :dataInicio)
""")
    Page<RelatorioProjection> findAllByTenantIdAndUserAccessToTheObraPaiWithFilters(
            Long tenantId,
            List<Long> obrasIdAllowed,
            boolean perfilIsAdministrador,
            boolean canViewOnlyAprovados,
            StatusRelatorio status,
            String obraExternalId,
            Long numero,
            LocalDate dataInicio,
            Pageable pageable
    );


    @Query("""
    SELECT
        COUNT(1) AS total,
        COALESCE(SUM(CASE WHEN r.status = 'ANDAMENTO' THEN 1 ELSE 0 END), 0) AS totalEmAndamento,
        COALESCE(SUM(CASE WHEN r.status = 'REVISAO' THEN 1 ELSE 0 END), 0) AS totalEmRevisao,
        COALESCE(SUM(CASE WHEN r.status = 'APROVADO' THEN 1 ELSE 0 END), 0) AS totalAprovados
    FROM RelatorioEntity r
    JOIN ObraEntity o ON o.id = r.obraId AND o.tenantId = r.tenantId
    WHERE r.tenantId = :tenantId
        AND (:isAdministrador = true OR o.id IN :obrasPermitidasIds)
        AND r.ativo IS TRUE
        AND (:canViewOnlyAprovados = false OR r.status = 'APROVADO')
        AND (:obraExternalId IS NULL OR o.idExterno = :obraExternalId)
""")
    RelatorioCountersProjection findCountByStatus(Long tenantId, List<Long> obrasPermitidasIds, boolean isAdministrador, Boolean canViewOnlyAprovados, String obraExternalId);

    @Query("""
    SELECT
        COUNT(cc.id) AS total,
        SUM(CASE WHEN cc.clima = 'CLARO' THEN 1 ELSE 0 END) AS totalClaro,
        SUM(CASE WHEN cc.clima = 'NUBLADO' THEN 1 ELSE 0 END) AS totalNublado,
        SUM(CASE WHEN cc.clima = 'CHUVOSO' THEN 1 ELSE 0 END) AS totalChuvoso,
        SUM(CASE WHEN cc.condicaoDoTempo = 'PRATICAVEL' THEN 1 ELSE 0 END) AS totalPraticavel,
        SUM(CASE WHEN cc.condicaoDoTempo = 'NAO_PRATICAVEL' THEN 1 ELSE 0 END) AS totalImpraticavel
    FROM RelatorioEntity r
    JOIN ObraEntity o ON o.id = r.obraId AND o.tenantId = r.tenantId
    JOIN CondicaoClimaticaEntity cc ON cc.id IN (r.caracteristicasManha.id, r.caracteristicasTarde.id, r.caracteristicasNoite.id)
       AND cc.ativo = TRUE
    WHERE r.tenantId = :tenantId
      AND (:obraExternalId IS NULL OR o.idExterno = :obraExternalId)
      AND r.ativo IS TRUE
      AND o.ativo IS TRUE
""")
    CondicaoClimaticaAnalysisProjection findCondicaoClimaticaAnalysis(Long tenantId, String obraExternalId);

    @Query("""
    SELECT r.idExterno
    FROM RelatorioEntity r
        WHERE r.tenantId = :tenantId
        AND r.obraId = :obraId
        AND r.ativo IS TRUE
        AND (:userCanViewOnlyAprovados = false OR r.status = 'APROVADO')
        AND (
            r.dataInicio > :dataInicioRelatorio
            OR (r.dataInicio = :dataInicioRelatorio AND r.enversCreatedDate > :dataCriacaoRelatorio)
        )
    ORDER BY r.dataInicio ASC, r.enversCreatedDate ASC
    LIMIT 1
""")
    Optional<String> findNextRelatorioExternalIdByObraIdAndDataRelatorio(Long tenantId, Long obraId, LocalDate dataInicioRelatorio, LocalDateTime dataCriacaoRelatorio, boolean userCanViewOnlyAprovados);

    @Query("""
    SELECT r.idExterno
    FROM RelatorioEntity r
        WHERE r.tenantId = :tenantId
        AND r.obraId = :obraId
        AND r.ativo IS TRUE
        AND (:userCanViewOnlyAprovados = false OR r.status = 'APROVADO')
        AND (
            r.dataInicio < :dataInicioRelatorio
            OR (r.dataInicio = :dataInicioRelatorio AND r.enversCreatedDate < :dataCriacaoRelatorio)
        )
    ORDER BY r.dataInicio DESC, r.enversCreatedDate DESC
    LIMIT 1
""")
    Optional<String> findPreviousRelatorioExternalIdByObraIdAndDataRelatorio(Long tenantId, Long obraId, LocalDate dataInicioRelatorio, LocalDateTime dataCriacaoRelatorio, boolean userCanViewOnlyAprovados);

}