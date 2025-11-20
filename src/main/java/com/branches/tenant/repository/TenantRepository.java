package com.branches.tenant.repository;

import com.branches.tenant.domain.TenantEntity;
import com.branches.tenant.repository.projection.TenantInfoProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.lang.ScopedValue;
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
        t.nomeFantasia AS nomeFantasia,
        t.cnpj AS cnpj,
        t.telefone AS telefone,
        t.logoUrl AS logoUrl,
        u.nome AS nomeUsuarioResponsavel,
        a AS assinaturaAtiva,
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
        ) AS quantidadeDeObrasCriadas
    FROM TenantEntity t
    JOIN UserEntity u ON t.userResponsavelId = u.id
    LEFT JOIN AssinaturaEntity a ON (t.id = a.tenantId AND a.status = 'ATIVO')
    WHERE t.id = :tenantId
        AND t.ativo IS TRUE
""")
    Optional<TenantInfoProjection> findTenantInfoById(Long tenantId);

    List<TenantEntity> findAllByIdInAndAtivoIsTrue(Collection<Long> ids);

    Optional<TenantEntity> findByIdExterno(String idExterno);
}
