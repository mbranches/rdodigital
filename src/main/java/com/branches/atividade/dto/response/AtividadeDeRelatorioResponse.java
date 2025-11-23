package com.branches.atividade.dto.response;

import com.branches.maodeobra.dto.response.MaoDeObraDeAtividadeResponse;
import com.branches.atividade.domain.AtividadeDeRelatorioEntity;
import com.branches.relatorio.domain.enums.StatusAtividade;
import com.branches.relatorio.dto.response.CampoPersonalizadoResponse;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

public record AtividadeDeRelatorioResponse(
        Long id,
        String descricao,
        Integer quantidadeRealizada,
        String unidadeMedida,
        BigDecimal porcentagemConcluida,
        StatusAtividade status,
        LocalTime horaInicio,
        LocalTime horaFim,
        LocalTime totalHoras,
        List<MaoDeObraDeAtividadeResponse> maoDeObra,
        List<CampoPersonalizadoResponse> camposPersonalizados
) {
    public static AtividadeDeRelatorioResponse from(AtividadeDeRelatorioEntity atividadeDeRelatorioEntity) {
        return new AtividadeDeRelatorioResponse(
                atividadeDeRelatorioEntity.getId(),
                atividadeDeRelatorioEntity.getDescricao(),
                atividadeDeRelatorioEntity.getQuantidadeRealizada(),
                atividadeDeRelatorioEntity.getUnidadeMedida(),
                atividadeDeRelatorioEntity.getPorcentagemConcluida(),
                atividadeDeRelatorioEntity.getStatus(),
                atividadeDeRelatorioEntity.getHoraInicio(),
                atividadeDeRelatorioEntity.getHoraFim(),
                atividadeDeRelatorioEntity.getTotalHoras(),
                atividadeDeRelatorioEntity.getMaoDeObra().stream()
                        .map(MaoDeObraDeAtividadeResponse::from)
                        .toList(),
                atividadeDeRelatorioEntity.getCamposPersonalizados().stream()
                        .map(CampoPersonalizadoResponse::from)
                        .toList()
        );
    }
}
