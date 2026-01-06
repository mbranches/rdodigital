package com.branches.assinatura.repository;

import com.branches.assinatura.domain.AssinaturaEntity;
import com.branches.assinatura.domain.enums.AssinaturaStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AssinaturaRepository extends JpaRepository<AssinaturaEntity, Long> {
    Optional<AssinaturaEntity> findByStatusAndTenantId(AssinaturaStatus status, Long tenantId);

    Optional<AssinaturaEntity> findByStripeSubscriptionId(String subscriptionId);
}
