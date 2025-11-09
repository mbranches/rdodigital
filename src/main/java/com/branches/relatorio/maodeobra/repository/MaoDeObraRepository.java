package com.branches.relatorio.maodeobra.repository;

import com.branches.relatorio.maodeobra.domain.MaoDeObraEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaoDeObraRepository extends JpaRepository<MaoDeObraEntity, Long> {
}