package com.branches.obra.repository.projections;

import com.branches.obra.domain.enums.StatusObra;

import java.time.LocalDate;

public interface ObraProjection {
    String getIdExterno();
    String getNome();
    String getCapaUrl();
    LocalDate getDataInicio();
    StatusObra getStatus();
    Long getQuantityOfRelatorios();
    Long getQuantityOfFotos();
}
