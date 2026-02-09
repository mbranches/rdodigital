package com.branches.arquivo.repository;

import com.branches.arquivo.domain.ArquivoEntity;
import com.branches.arquivo.domain.enums.TipoArquivo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ArquivoRepository extends JpaRepository<ArquivoEntity, Long> {
    Optional<ArquivoEntity> findByIdAndRelatorioIdAndTipoArquivo(Long id, Long relatorioId, TipoArquivo tipoArquivo);

    List<ArquivoEntity> findAllByRelatorioIdAndTipoArquivoOrderByEnversCreatedDateDesc(Long relatorioId, TipoArquivo tipoArquivo);

    Optional<ArquivoEntity> findByIdAndRelatorioId(Long arquivoId, Long id);

    List<ArquivoEntity> findAllByRelatorioIdOrderByEnversCreatedDateDesc(Long relatorioId);

    @Query("""
    SELECT a
    FROM ArquivoEntity a
    WHERE a.relatorio.obraId = :id
    AND a.tipoArquivo = 'FOTO'
    AND a.relatorio.ativo IS TRUE
    ORDER BY a.enversCreatedDate DESC
    LIMIT 5
""")
    List<ArquivoEntity> findTop5FotosDeRelatoriosByObraId(Long id);

    @Query("""
    SELECT a
    FROM ArquivoEntity a
    WHERE a.relatorio.obraId = :id
        AND a.tipoArquivo = :tipo
        AND a.tenantId = :tenantId
    """)
    Page<ArquivoEntity> findAllByObraIdAndTipoArquivoAndTenantId(Long id, TipoArquivo tipo, Long tenantId, Pageable pageable);

    @Query("""
    SELECT a
    FROM ArquivoEntity a
    WHERE a.relatorio.obraId = :id
        AND a.tipoArquivo = :tipo
        AND a.tenantId = :tenantId
        AND a.relatorio.status = 'APROVADO'
    """)
    Page<ArquivoEntity> findAllByObraIdAndTipoArquivoAndTenantIdAndRelatorioIsAprovado(Long id, TipoArquivo tipo, Long tenantId, Pageable pageable);

}