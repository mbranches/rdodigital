package com.branches.relatorio.repository.projections;

import com.branches.relatorio.domain.enums.StatusRelatorio;

import java.time.LocalDate;

public interface RelatorioProjection {
    String getIdExterno();

    LocalDate getDataInicio();

    LocalDate getDataFim();

    Long getNumero();

    StatusRelatorio getStatus();

    String getPdfUrl();

    String getObraIdExterno();

    String getObraNome();

    String getObraEndereco();

    String getObraContratante();

    String getObraResponsavel();

    String getObraNumeroContrato();
}
