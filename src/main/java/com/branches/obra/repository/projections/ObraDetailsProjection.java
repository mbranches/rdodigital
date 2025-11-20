package com.branches.obra.repository.projections;

import com.branches.obra.domain.GrupoDeObraEntity;
import com.branches.obra.domain.enums.StatusObra;
import com.branches.obra.domain.enums.TipoContratoDeObra;
import com.branches.obra.domain.enums.TipoMaoDeObra;

import java.time.LocalDate;

public interface ObraDetailsProjection {
    Long getId();
    String getIdExterno();
    String getNome();
    String getResponsavel();
    String getContratante();
    TipoContratoDeObra getTipoContrato();
    LocalDate getDataInicio();
    LocalDate getDataPrevistaFim();
    LocalDate getDataFimReal();
    String getNumeroContrato();
    String getEndereco();
    String getObservacoes();
    String getCapaUrl();
    StatusObra getStatus();
    TipoMaoDeObra getTipoMaoDeObra();
    GrupoDeObraEntity getGrupoDeObra();
    Long getQuantidadeRelatorios();
    Long getQuantidadeAtividades();
    Long getQuantidadeOcorrencias();
    Long getQuantidadeComentarios();
}
