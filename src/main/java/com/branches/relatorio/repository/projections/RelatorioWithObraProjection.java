package com.branches.relatorio.repository.projections;

import com.branches.obra.domain.ObraEntity;
import com.branches.relatorio.domain.RelatorioEntity;

public interface RelatorioWithObraProjection {
    RelatorioEntity getRelatorio();

    ObraEntity getObra();
}
