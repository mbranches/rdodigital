package com.branches.obra.repository;

import com.branches.obra.domain.ObraEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ObraRepository extends JpaRepository<ObraEntity, Long> {
    Integer countByTenantIdAndAtivoIsTrue(Long tenantId);

    Optional<ObraEntity> findByIdExternoAndTenantId(String idExterno, Long tenantId);
}
