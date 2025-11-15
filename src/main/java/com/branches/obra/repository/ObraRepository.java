package com.branches.obra.repository;

import com.branches.obra.domain.ObraEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ObraRepository extends JpaRepository<ObraEntity, Long> {
    Integer countByTenantIdAndAtivoIsTrue(Long tenantId);

    Optional<ObraEntity> findByIdExternoAndTenantId(String idExterno, Long tenantId);

    List<ObraEntity> findAllByTenantId(Long tenantId);

    List<ObraEntity> findAllByTenantIdAndIdIn(Long tenantId, Collection<Long> ids);

    List<ObraEntity> findAllByIdExternoInAndTenantIdAndAtivoIsTrue(Collection<String> obrasExternalIds, Long tenantId);
}
