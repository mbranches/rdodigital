package com.branches.comentarios.repository;

import com.branches.comentarios.model.ComentarioDeRelatorioEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComentarioDeRelatorioRepository extends JpaRepository<ComentarioDeRelatorioEntity, Long> {
    List<ComentarioDeRelatorioEntity> findAllByRelatorioId(Long relatorioId);

    Optional<ComentarioDeRelatorioEntity> findByIdAndRelatorioId(Long id, Long relatorioId);

    @Query("""
        SELECT c
        FROM ComentarioDeRelatorioEntity c
        WHERE c.tenantId = :tenantId
        AND c.relatorio.obraId = :obraId
        AND (:canViewOnlyAprovados = false OR c.relatorio.status = 'APROVADO')
""")
    Page<ComentarioDeRelatorioEntity> findAllByObraIdAndTenantId(Long obraId, Long tenantId, Boolean canViewOnlyAprovados, Pageable pageRequest);
}