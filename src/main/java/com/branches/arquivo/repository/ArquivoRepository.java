package com.branches.arquivo.repository;

import com.branches.arquivo.domain.ArquivoEntity;
import com.branches.arquivo.domain.enums.TipoArquivo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArquivoRepository extends JpaRepository<ArquivoEntity, Long> {
    Optional<ArquivoEntity> findByIdAndRelatorioIdAndTipoArquivo(Long id, Long relatorioId, TipoArquivo tipoArquivo);

    List<ArquivoEntity> findAllByRelatorioIdAndTipoArquivoOrderByEnversCreatedDateDesc(Long relatorioId, TipoArquivo tipoArquivo);
}