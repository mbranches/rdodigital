package com.branches.assinaturadeplano.repository;

import com.branches.assinaturadeplano.domain.AssinaturaDePlanoEntity;
import com.branches.assinaturadeplano.domain.enums.AssinaturaStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssinaturaDePlanoRepository extends JpaRepository<AssinaturaDePlanoEntity, Long> {
    Optional<AssinaturaDePlanoEntity> findByStatusInAndTenantId(List<AssinaturaStatus> statusList, Long tenantId);

    Optional<AssinaturaDePlanoEntity> findByStripeSubscriptionId(String subscriptionId);

    @Query("""
    SELECT a
    FROM AssinaturaDePlanoEntity a
    WHERE a.plano.recorrencia = 'MENSAL_AVULSO'
        AND a.status = 'ATIVO'
         AND a.dataFim < :dataFim
    """)
    List<AssinaturaDePlanoEntity> findAssinaturasDePlanosMensalAvulsoAtivasComDataFimBefore(LocalDate dataFim);

    boolean existsByStatusInAndTenantId(Collection<AssinaturaStatus> status, Long tenantId);

    boolean existsByTenantIdInAndStatusIn(Collection<Long> tenantIds, Collection<AssinaturaStatus> status);

    boolean existsByStripeSubscriptionId(String stripeSubscriptionId);
}
