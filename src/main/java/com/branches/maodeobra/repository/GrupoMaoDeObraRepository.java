package com.branches.maodeobra.repository;

import com.branches.maodeobra.domain.GrupoMaoDeObraEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GrupoMaoDeObraRepository extends JpaRepository<GrupoMaoDeObraEntity, Long> {
    Optional<GrupoMaoDeObraEntity> findByIdAndTenantIdAndAtivoIsTrue(Long id, Long tenantId);

    List<GrupoMaoDeObraEntity> findAllByTenantIdAndAtivoIsTrue(Long tenantId);
}
