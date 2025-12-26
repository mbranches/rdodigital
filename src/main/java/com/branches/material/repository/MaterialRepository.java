package com.branches.material.repository;

import com.branches.material.domain.MaterialEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaterialRepository extends JpaRepository<MaterialEntity, Long> {
    Optional<MaterialEntity> findByIdAndTenantIdAndAtivoIsTrue(Long id, Long tenantId);

    List<MaterialEntity> findAllByTenantIdAndAtivoIsTrue(Long tenantId);
}

