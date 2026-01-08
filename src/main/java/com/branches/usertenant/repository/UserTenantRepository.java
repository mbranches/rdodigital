package com.branches.usertenant.repository;

import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.domain.UserTenantKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserTenantRepository extends JpaRepository<UserTenantEntity, UserTenantKey> {
    long countByTenantId(Long tenantId);

    @Query("""
    SELECT ut
    FROM UserTenantEntity ut
    WHERE ut.user.idExterno = :userIdExterno
    AND ut.tenantId = :tenantId
""")
    Optional<UserTenantEntity> findByUserIdExternoAndTenantId(String userIdExterno, Long tenantId);

    List<UserTenantEntity> findAllByTenantId(Long tenantId);

    @Query("""
    SELECT DISTINCT ut
    FROM UserTenantEntity ut
    LEFT JOIN ut.userObraPermitidaEntities op
    WHERE ut.tenantId = :tenantId
    AND (op.obraId = :id OR ut.perfil = 'ADMINISTRADOR')
""")
    List<UserTenantEntity> findAllWithAccessToObra(Long tenantId, Long id);
}
