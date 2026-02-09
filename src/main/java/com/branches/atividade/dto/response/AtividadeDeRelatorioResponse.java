package com.branches.atividade.dto.response;

import com.branches.atividade.domain.AtividadeDeRelatorioCampoPersonalizadoEntity;
import com.branches.maodeobra.dto.response.MaoDeObraDeAtividadeResponse;
import com.branches.atividade.domain.AtividadeDeRelatorioEntity;
import com.branches.atividade.domain.enums.StatusAtividade;
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
        Integer minutosTotais,
        List<MaoDeObraDeAtividadeResponse> maoDeObra,
        List<FotoDeAtividadeResponse> fotos,
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
                atividadeDeRelatorioEntity.getMinutosTotais(),
                atividadeDeRelatorioEntity.getMaoDeObra().stream()
                        .map(MaoDeObraDeAtividadeResponse::from)
                        .toList(),
                atividadeDeRelatorioEntity.getFotos().stream()
                        .map(FotoDeAtividadeResponse::from)
                        .toList(),
                atividadeDeRelatorioEntity.getCamposPersonalizados().stream()
                        .map(AtividadeDeRelatorioCampoPersonalizadoEntity::getCampoPersonalizado)
                        .map(CampoPersonalizadoResponse::from)
                        .toList()
        );
    }
}
