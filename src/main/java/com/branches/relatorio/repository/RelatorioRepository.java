package com.branches.relatorio.repository;

import com.branches.relatorio.domain.RelatorioEntity;
import com.branches.relatorio.domain.enums.StatusRelatorio;
import com.branches.relatorio.repository.projections.RelatorioDetailsProjection;
import com.branches.relatorio.repository.projections.RelatorioProjection;
import com.branches.relatorio.repository.projections.RelatorioWithObraProjection;
import org.springframework.data.domain.Page;
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
        t.id AS tenantId,
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
        r.horasIntervalo AS horasIntervalo,
        r.horasTrabalhadas AS horasTrabalhadas,
        r.status AS status,
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
    SELECT r.id AS id,
        r.idExterno AS idExterno,
        t.id AS tenantId,
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
        r.horasIntervalo AS horasIntervalo,
        r.horasTrabalhadas AS horasTrabalhadas,
        r.status AS status,
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
    WHERE r.id = :id
      AND t.ativo IS TRUE
""")
    Optional<RelatorioDetailsProjection> findDetailsById(Long id);

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
    Page<RelatorioProjection> findAllByObraIdAndStatusProjection(Long obraId, StatusRelatorio statusRelatorio, Pageable pageable);

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
        o.numeroContrato AS obraNumeroContrato
    FROM RelatorioEntity r
    JOIN ObraEntity o ON o.id = r.obraId AND o.tenantId = r.tenantId
    WHERE r.obraId = :obraId
        AND r.ativo IS TRUE
""")
    Page<RelatorioProjection> findAllByObraIdProjection(Long obraId, Pageable pageable);

    @Query("""
        SELECT r as relatorio,
               o as obra
        FROM RelatorioEntity r
            JOIN ObraEntity o ON o.id = r.obraId
        WHERE r.idExterno = :relatorioExternalId
          AND r.tenantId = :tenantId
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
        o.numeroContrato AS obraNumeroContrato
    FROM RelatorioEntity r
    JOIN ObraEntity o ON o.id = r.obraId AND o.tenantId = r.tenantId
    WHERE r.tenantId = :tenantId
      AND r.status = 'APROVADO'
      AND (:perfilIsAdministrador = true OR o.id IN :obrasIdAllowed)
      AND r.ativo IS TRUE
       
""")
    Page<RelatorioProjection> findAllByTenantIdAndIsAprovadoAndUserAccessToTheObraPai(Long tenantId, Long userId, List<Long> obrasIdAllowed, boolean perfilIsAdministrador, Pageable pageable);

    @Query("""
    SELECT r.idExterno AS idExterno,
        r.dataInicio AS dataInicio,
        r.dataFim AS dataFim,
        r.numero AS numero,
        r.status AS status,
        a.arquivoUrl AS pdfUrl,
        o.idExterno AS obraIdExterno,
        o.nome AS obraNome,
        o.endereco AS obraEndereco,
        o.contratante AS obraContratante,
        o.responsavel AS obraResponsavel,
        o.numeroContrato AS obraNumeroContrato
    FROM RelatorioEntity r
    JOIN ObraEntity o ON o.id = r.obraId AND o.tenantId = r.tenantId
    JOIN ArquivoDeRelatorioDeUsuarioEntity a ON a.userId = :userId AND a.relatorioId = r.id
    WHERE r.tenantId = :tenantId
        AND (:perfilIsAdministrador = true OR o.id IN :obrasIdAllowed)
        AND r.ativo IS TRUE
""")
    Page<RelatorioProjection> findAllByTenantIdAndUserAccessToTheObraPai(Long tenantId, Long userId, List<Long> obrasIdAllowed, boolean perfilIsAdministrador, Pageable pageable);
}