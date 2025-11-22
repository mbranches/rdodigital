package com.branches.relatorio.maodeobra.repository;

import com.branches.obra.domain.enums.TipoMaoDeObra;
import com.branches.relatorio.maodeobra.domain.MaoDeObraEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaoDeObraRepository extends JpaRepository<MaoDeObraEntity, Long> {
    List<MaoDeObraEntity> findAllByTenantIdAndTipoAndAtivoIsTrue(Long tenantId, TipoMaoDeObra tipo);

    Optional<MaoDeObraEntity> findByIdAndTenantIdAndAtivoIsTrue(Long id, Long tenantId);

    Optional<MaoDeObraEntity> findByIdAndTenantIdAndTipoAndAtivoIsTrue(Long id, Long tenantId, TipoMaoDeObra type);
}