package com.branches.equipamento.repository;

import com.branches.equipamento.domain.EquipamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface EquipamentoRepository extends JpaRepository<EquipamentoEntity, Long> {
    Optional<EquipamentoEntity> findByIdAndTenantIdAndAtivoIsTrue(Long id, Long tenantId);

    List<EquipamentoEntity> findAllByTenantIdAndAtivoIsTrue(Long tenantId);

    List<EquipamentoEntity> findAllByIdInAndTenantIdAndAtivoIsTrue(Collection<Long> equipamentosIds, Long tenantId);
}
