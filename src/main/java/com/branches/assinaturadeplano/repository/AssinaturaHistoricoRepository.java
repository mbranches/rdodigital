package com.branches.assinaturadeplano.repository;

import com.branches.assinaturadeplano.domain.AssinaturaHistoricoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssinaturaHistoricoRepository extends JpaRepository<AssinaturaHistoricoEntity, Long> {
}
