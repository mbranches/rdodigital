package com.branches.obra.repository.projections;

import com.branches.obra.domain.ConfiguracaoRelatoriosEntity;
import com.branches.obra.domain.enums.StatusObra;

import java.time.LocalDate;

public interface ObraProjection {
    String getIdExterno();

    String getNome();

    String getCapaUrl();

    LocalDate getDataInicio();
    LocalDate getDataPrevistaFim();
    LocalDate getDataFimReal();

    StatusObra getStatus();

    ConfiguracaoRelatoriosEntity getConfiguracaoRelatorios();

    Long getQuantityOfRelatorios();
    Long getQuantityOfFotos();
    LocalDate getDataUltimoRelatorio();
}
