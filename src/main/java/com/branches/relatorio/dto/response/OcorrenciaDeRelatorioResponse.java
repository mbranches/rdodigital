package com.branches.relatorio.dto.response;

import com.branches.relatorio.domain.OcorrenciaDeRelatorioEntity;
import com.branches.ocorrencia.dto.response.TipoDeOcorrenciaResponse;

import java.time.LocalTime;
import java.util.List;

public record OcorrenciaDeRelatorioResponse(
        Long id,
        String descricao,
        List<TipoDeOcorrenciaResponse> tiposOcorrencia,
        LocalTime horaInicio,
        LocalTime horaFim,
        List<CampoPersonalizadoResponse> camposPersonalizados
) {
    public static OcorrenciaDeRelatorioResponse from(OcorrenciaDeRelatorioEntity ocorrenciaDeRelatorioEntity) {
        var tiposOcorrencia = ocorrenciaDeRelatorioEntity.getTiposDeOcorrencia() != null ?
                ocorrenciaDeRelatorioEntity.getTiposDeOcorrencia().stream().map(TipoDeOcorrenciaResponse::from).toList()
        : null;

        var camposPersonalizados = ocorrenciaDeRelatorioEntity.getCamposPersonalizados() != null ?
                ocorrenciaDeRelatorioEntity.getCamposPersonalizados().stream()
                        .map(CampoPersonalizadoResponse::from)
                        .toList() : null;

        return new OcorrenciaDeRelatorioResponse(
                ocorrenciaDeRelatorioEntity.getId(),
                ocorrenciaDeRelatorioEntity.getDescricao(),
                tiposOcorrencia,
                ocorrenciaDeRelatorioEntity.getHoraInicio(),
                ocorrenciaDeRelatorioEntity.getHoraFim(),
                camposPersonalizados
        );
    }
}
