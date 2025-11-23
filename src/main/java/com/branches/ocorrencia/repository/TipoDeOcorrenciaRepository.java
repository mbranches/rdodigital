package com.branches.ocorrencia.repository;

import com.branches.ocorrencia.domain.TipoDeOcorrenciaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface TipoDeOcorrenciaRepository extends JpaRepository<TipoDeOcorrenciaEntity, Long> {
    Optional<TipoDeOcorrenciaEntity> findByIdAndTenantIdAndAtivoIsTrue(Long id, Long tenantId);

    List<TipoDeOcorrenciaEntity> findAllByTenantIdAndAtivoIsTrue(Long tenantId);

    List<TipoDeOcorrenciaEntity> findAllByIdInAndTenantId(Collection<Long> ids, Long tenantId);
}
