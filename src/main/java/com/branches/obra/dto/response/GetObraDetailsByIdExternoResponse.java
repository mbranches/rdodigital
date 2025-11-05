package com.branches.obra.dto.response;

import com.branches.obra.domain.ObraEntity;
import com.branches.obra.domain.StatusObra;
import com.branches.obra.domain.TipoContratoDeObra;
import com.branches.shared.enums.TipoMaoDeObra;

import java.time.LocalDate;

public record GetObraDetailsByIdExternoResponse(
    String idExterno,
    String nome,
    String responsavel,
    String contratante,
    TipoContratoDeObra tipoContrato,
    LocalDate dataInicio,
    LocalDate dataPrevistaFim,
    String numeroContrato,
    String endereco,
    String observacoes,
    String capaUrl,
    StatusObra status,
    TipoMaoDeObra tipoMaoDeObra,
    GrupoDeObraResponse grupoDeObra
) {
    public static GetObraDetailsByIdExternoResponse from(ObraEntity obra) {
        return new GetObraDetailsByIdExternoResponse(
            obra.getIdExterno(),
            obra.getNome(),
            obra.getResponsavel(),
            obra.getContratante(),
            obra.getTipoContrato(),
            obra.getDataInicio(),
            obra.getDataPrevistaFim(),
            obra.getNumeroContrato(),
            obra.getEndereco(),
            obra.getObservacoes(),
            obra.getCapaUrl(),
            obra.getStatus(),
            obra.getTipoMaoDeObra(),
            obra.getGrupo() != null ? GrupoDeObraResponse.from(obra.getGrupo()) : null
        );
    }
}
