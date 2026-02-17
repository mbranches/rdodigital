package com.branches.plano.repository;

import com.branches.plano.domain.PlanoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanoRepository extends JpaRepository<PlanoEntity, Long> {
    Optional<PlanoEntity> findByNome(String nome);

    @Query("""
    SELECT p FROM PlanoEntity p
    WHERE p.ativo = true
    ORDER BY CASE
        WHEN p.recorrencia = 'MENSAL' OR p.recorrencia = 'MENSAL_AVULSO' THEN p.valor
        WHEN p.recorrencia = 'ANUAL' THEN p.valor / 12
        WHEN p.recorrencia = 'SEMANAL' THEN p.valor * 4
        WHEN p.recorrencia = 'DIARIO' THEN p.valor * 30
        ELSE p.valor
    END ASC
""")
    List<PlanoEntity> findAllByAtivoIsTrueOrderByValorPorMesAsc();

    boolean existsByNome(String nome);

    Optional<PlanoEntity> findByStripePriceId(String stripeId);
}
