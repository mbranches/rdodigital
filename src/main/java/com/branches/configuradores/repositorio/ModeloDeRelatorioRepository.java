package com.branches.configuradores.repositorio;

import com.branches.configuradores.domain.ModeloDeRelatorioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModeloDeRelatorioRepository extends JpaRepository<ModeloDeRelatorioEntity, Long> {
    boolean existsByTituloAndTenantIdAndAtivoIsTrue(String titulo, Long tenantId);

    Optional<ModeloDeRelatorioEntity> findByIdAndTenantIdAndAtivoIsTrue(Long id, Long tenantId);

    boolean existsByTituloAndTenantIdAndIdIsNotAndAtivoIsTrue(String titulo, Long tenantId, Long id);

    List<ModeloDeRelatorioEntity> findAllByTenantIdAndAtivoIsTrueOrderByEnversCreatedDateAsc(Long tenantId);
}
