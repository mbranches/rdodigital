package com.branches.plano.repository;

import com.branches.plano.domain.PlanoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanoRepository extends JpaRepository<PlanoEntity, Long> {
    Optional<PlanoEntity> findByNome(String nome);

    List<PlanoEntity> findAllByAtivoIsTrueOrderByValorAsc();
}
