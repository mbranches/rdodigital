package com.branches.suporte.repository;

import com.branches.suporte.entity.TicketDeSuporteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketDeSuporteRepository extends JpaRepository<TicketDeSuporteEntity, Long> {
}