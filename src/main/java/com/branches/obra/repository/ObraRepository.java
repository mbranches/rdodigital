package com.branches.obra.repository;

import com.branches.obra.domain.ObraEntity;
import com.branches.obra.repository.projections.ObraDetailsProjection;
import com.branches.obra.repository.projections.ObraProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ObraRepository extends JpaRepository<ObraEntity, Long> {
    Integer countByTenantIdAndAtivoIsTrue(Long tenantId);

    Optional<ObraEntity> findByIdExternoAndTenantIdAndAtivoIsTrue(String idExterno, Long tenantId);

    List<ObraEntity> findAllByIdExternoInAndTenantIdAndAtivoIsTrue(Collection<String> obrasExternalIds, Long tenantId);

    @Query("""
    SELECT o.idExterno AS idExterno,
        o.nome AS nome,
        o.status AS status,
        o.capaUrl AS capaUrl,
        o.dataInicio AS dataInicio,
        o.dataPrevistaFim AS dataPrevistaFim,
        (
            SELECT COUNT(1)
            FROM RelatorioEntity r
            WHERE r.obraId = o.id AND r.ativo IS TRUE
        ) AS quantityOfRelatorios,
        (
            SELECT COUNT(1)
            FROM ArquivoEntity a
            WHERE a.relatorio.obraId = o.id
            AND a.tipoArquivo = 'FOTO'
        ) AS quantityOfFotos,
        (
            SELECT r.dataInicio
            FROM RelatorioEntity r
            WHERE r.obraId = o.id AND r.ativo IS TRUE
            ORDER BY r.dataInicio DESC
            LIMIT 1
        ) AS dataUltimoRelatorio
    FROM ObraEntity o
    WHERE o.tenantId = :tenantId AND o.ativo IS TRUE
""")
    List<ObraProjection> findAllByTenantIdProjection(Long tenantId);

    @Query("""
    SELECT o.idExterno AS idExterno,
        o.nome AS nome,
        o.status AS status,
        o.capaUrl AS capaUrl,
        o.dataInicio AS dataInicio,
        o.dataPrevistaFim AS dataPrevistaFim,
        (
            SELECT COUNT(1)
            FROM RelatorioEntity r
            WHERE r.obraId = o.id AND r.ativo IS TRUE
        ) AS quantityOfRelatorios,
        (
            SELECT COUNT(1)
            FROM ArquivoEntity a
            WHERE a.relatorio.obraId = o.id
            AND a.tipoArquivo = 'FOTO'
        ) AS quantityOfFotos,
        (
            SELECT r.dataInicio
            FROM RelatorioEntity r
            WHERE r.obraId = o.id AND r.ativo IS TRUE
            ORDER BY r.dataInicio DESC
            LIMIT 1
        ) AS dataUltimoRelatorio
    FROM ObraEntity o
    WHERE o.id IN :userAllowedObrasIds
        AND o.tenantId = :tenantId
        AND o.ativo IS TRUE
""")
    List<ObraProjection> findAllByTenantIdAndIdInProjection(Long tenantId, List<Long> userAllowedObrasIds);

    @Query("""
    SELECT o.id AS id,
        o.idExterno AS idExterno,
        o.nome AS nome,
        o.responsavel AS responsavel,
        o.contratante AS contratante,
        o.tipoContrato AS tipoContrato,
        o.dataInicio AS dataInicio,
        o.dataPrevistaFim AS dataPrevistaFim,
        o.numeroContrato AS numeroContrato,
        o.endereco AS endereco,
        o.observacoes AS observacoes,
        o.capaUrl AS capaUrl,
        o.status AS status,
        o.tipoMaoDeObra AS tipoMaoDeObra,
        o.grupo AS grupoDeObra,
        o.dataFimReal AS dataFimReal,
        (
            SELECT COUNT(1)
            FROM RelatorioEntity r
            WHERE r.obraId = o.id AND r.ativo IS TRUE
        ) AS quantidadeRelatorios,
        (
            SELECT COUNT(1)
            FROM AtividadeDeRelatorioEntity a
            WHERE a.relatorio.obraId = o.id AND a.relatorio.ativo IS TRUE
        ) AS quantidadeAtividades,
        (
            SELECT COUNT(1)
            FROM OcorrenciaDeRelatorioEntity odr
            WHERE odr.relatorio.obraId = o.id AND odr.relatorio.ativo IS TRUE
        ) AS quantidadeOcorrencias,
        (
            SELECT COUNT(1)
            FROM ComentarioDeRelatorioEntity cdr
            WHERE cdr.relatorio.obraId = o.id AND cdr.relatorio.ativo IS TRUE
        ) AS quantidadeComentarios,
        (
            SELECT COUNT(1)
            FROM ArquivoEntity a
            WHERE a.tipoArquivo = 'FOTO'
                AND a.relatorio.obraId = o.id
                AND a.relatorio.ativo IS TRUE
        ) AS quantidadeFotos
    FROM ObraEntity o
    LEFT JOIN o.grupo
    WHERE o.idExterno = :idExterno
      AND o.tenantId = :tenantDaObraId
      AND o.ativo IS TRUE
""")
    Optional<ObraDetailsProjection> findObraDetailsByIdExternoAndTenantId(String idExterno, Long tenantDaObraId);

    @Query("""
    SELECT o.id
    FROM ObraEntity o
    WHERE o.idExterno = :idExterno
      AND o.tenantId = :tenantId
      AND o.ativo IS TRUE
""")
    Optional<Long> findIdByIdExternoAndTenantId(String idExterno, Long tenantId);

    Optional<ObraEntity> findByIdAndTenantId(Long id, Long tenantId);
}
