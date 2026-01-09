package com.branches.tenant.repository;

import com.branches.tenant.domain.TenantEntity;
import com.branches.tenant.repository.projection.TenantInfoProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface TenantRepository extends JpaRepository<TenantEntity, Long> {
    @Query("""
    SELECT t.id
    FROM TenantEntity t
    WHERE t.idExterno = :idExterno
        AND t.ativo IS TRUE
""")
    Optional<Long> findTenantIdByIdExternoAndAtivoIsTrue(String idExterno);

    @Query("""
    SELECT t.idExterno AS idExterno,
        t.razaoSocial AS razaoSocial,
        t.nome AS nome,
        t.cnpj AS cnpj,
        t.telefone AS telefone,
        t.logoUrl AS logoUrl,
        u AS responsavel,
        a AS assinaturaCorrente,
        pt AS periodoDeTeste,
        EXISTS (
            SELECT 1
            FROM AssinaturaDePlanoEntity a2
            WHERE a2.tenantId = t.id
                AND a2.status NOT IN ('INCOMPLETO', 'PENDENTE', 'NAO_INICIADO')
        ) AS alreadyHadSubscription,
        (
            SELECT COUNT(1)
            FROM UserTenantEntity ut2
            WHERE ut2.tenantId = t.id
                AND ut2.ativo IS TRUE
        ) AS quantidadeDeUsersCriados,
        (
            SELECT COUNT(1)
            FROM ObraEntity o
            WHERE o.tenantId = t.id
                AND o.ativo IS TRUE
        ) AS quantidadeDeObrasCriadas,
        (
            SELECT COUNT(1)
            FROM RelatorioEntity r
            WHERE r.tenantId = t.id
                AND r.ativo IS TRUE
        ) AS quantidadeDeRelatoriosCriados
    FROM TenantEntity t
    JOIN UserEntity u ON t.userResponsavelId = u.id
    LEFT JOIN AssinaturaDePlanoEntity a ON (t.id = a.tenantId AND a.status NOT IN ('INCOMPLETO', 'PENDENTE', 'NAO_INICIADO', 'CANCELADO', 'ENCERRADO', 'SUSPENSO'))
    LEFT JOIN PeriodoTesteEntity pt ON t.id = pt.tenantId
    WHERE t.id = :tenantId
        AND t.ativo IS TRUE
""")
    Optional<TenantInfoProjection> findTenantInfoById(Long tenantId);

    List<TenantEntity> findAllByIdInAndAtivoIsTrue(Collection<Long> ids);

    Optional<TenantEntity> findByIdExterno(String idExterno);

    boolean existsByCnpj(String cnpj);

    boolean existsByTelefone(String telefone);
}
