package com.branches.obra.repository;

import com.branches.obra.domain.GrupoDeObraEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GrupoDeObraRepository extends JpaRepository<GrupoDeObraEntity, Long> {
    Optional<GrupoDeObraEntity> findByIdAndTenantId(Long id, Long tenantId);
}
