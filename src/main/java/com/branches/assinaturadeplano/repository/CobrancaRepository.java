package com.branches.assinaturadeplano.repository;

import com.branches.assinaturadeplano.domain.CobrancaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CobrancaRepository extends JpaRepository<CobrancaEntity, Long> {
    boolean existsByStripeInvoiceId(String stripeInvoiceId);

    Optional<CobrancaEntity> findByStripeInvoiceId(String stripeInvoiceId);
}