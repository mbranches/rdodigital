package com.branches.obra.repository.projections;

import com.branches.obra.domain.enums.StatusObra;

public interface ObraProjection {
    String getIdExterno();
    String getNome();
    String getCapaUrl();
    StatusObra getStatus();
    Long getQuantityOfRelatorios();
    Long getQuantityOfFotos();
}
