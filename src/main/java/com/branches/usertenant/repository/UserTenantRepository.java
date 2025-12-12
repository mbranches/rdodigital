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
    long countByTenantIdAndAtivoIsTrue(Long tenantId);

    Optional<UserTenantEntity> findByUserIdExternoAndTenantId(String userIdExterno, Long tenantId);

    List<UserTenantEntity> findAllByTenantId(Long tenantId);

    @Query("""
    SELECT DISTINCT ut
    FROM UserTenantEntity ut
    JOIN ut.userObraPermitidaEntities op
    where op.obraId = :id
""")
    List<UserTenantEntity> findAllWithAccessToObra(Long id);
}
