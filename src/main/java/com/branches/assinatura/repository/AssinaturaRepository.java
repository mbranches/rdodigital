package com.branches.assinatura.repository;

import com.branches.assinatura.domain.AssinaturaEntity;
import com.branches.assinatura.domain.enums.AssinaturaStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssinaturaRepository extends JpaRepository<AssinaturaEntity, Long> {
    Optional<AssinaturaEntity> findByStatusInAndTenantId(List<AssinaturaStatus> statusList, Long tenantId);

    Optional<AssinaturaEntity> findByStripeSubscriptionId(String subscriptionId);

    @Query("""
    SELECT a
    FROM AssinaturaEntity a
    WHERE a.plano.recorrencia = 'MENSAL_AVULSO'
        AND a.status = 'ATIVO'
         AND a.dataFim < :dataFim
    """)
    List<AssinaturaEntity> findAssinaturasDePlanosMensalAvulsoAtivasComDataFimBefore(LocalDate dataFim);
}
