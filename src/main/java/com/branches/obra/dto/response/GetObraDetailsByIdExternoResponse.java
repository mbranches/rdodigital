package com.branches.obra.dto.response;

import com.branches.obra.domain.enums.StatusObra;
import com.branches.obra.domain.enums.TipoContratoDeObra;
import com.branches.obra.domain.enums.TipoMaoDeObra;
import com.branches.obra.repository.projections.ObraDetailsProjection;
import com.branches.relatorio.rdo.dto.response.RelatorioResponse;
import com.branches.relatorio.rdo.repository.projections.RelatorioProjection;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public record GetObraDetailsByIdExternoResponse(
        String idExterno,
        String nome,
        String responsavel,
        String contratante,
        TipoContratoDeObra tipoContrato,
        LocalDate dataInicio,
        LocalDate dataPrevistaFim,
        Long prazoContratual,
        Long prazoPraVencer,
        Long prazoDecorrido,
        BigDecimal porcentagemPrazoDecorrido,
        String numeroContrato,
        String endereco,
        String observacoes,
        String capaUrl,
        StatusObra status,
        TipoMaoDeObra tipoMaoDeObra,
        GrupoDeObraResponse grupoDeObra,
        Long quantidadeRelatorios,
        Long quantidadeAtividades,
        Long quantidadeOcorrencias,
        Long quantidadeComentarios,
        //todo: Long quantidadeFotos,
        List<RelatorioResponse> relatoriosRecentes //todo: limitar a 5 mais recentes
//    List<FotoObraResponse> fotosRecentes //todo: implementar e  limitar a 5 mais recentes
) {
    public static GetObraDetailsByIdExternoResponse from(ObraDetailsProjection obra, List<RelatorioProjection> relatoriosRecentesProjections) {
        long prazoContratual = ChronoUnit.DAYS.between(obra.getDataInicio(), obra.getDataPrevistaFim());

        long diferencaEntreHojeEDataFim = ChronoUnit.DAYS.between(LocalDate.now(), obra.getDataPrevistaFim());

        Long prazoPraVencer = diferencaEntreHojeEDataFim < 0 ? 0L : diferencaEntreHojeEDataFim;

        LocalDate dataPraCompararDiasDecorrido = obra.getDataFimReal() != null ? obra.getDataFimReal() : LocalDate.now();
        long prazoDecorrido = ChronoUnit.DAYS.between(obra.getDataInicio(), dataPraCompararDiasDecorrido);

        BigDecimal porcentagemPrazoDecorrido = prazoContratual == 0 ? BigDecimal.valueOf(100) :
                BigDecimal.valueOf(prazoDecorrido)
                        .multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(prazoContratual), 2, RoundingMode.HALF_UP);

        return new GetObraDetailsByIdExternoResponse(
            obra.getIdExterno(),
            obra.getNome(),
            obra.getResponsavel(),
            obra.getContratante(),
            obra.getTipoContrato(),
            obra.getDataInicio(),
            obra.getDataPrevistaFim(),
            prazoContratual,
            prazoPraVencer,
            prazoDecorrido,
            porcentagemPrazoDecorrido,
            obra.getNumeroContrato(),
            obra.getEndereco(),
            obra.getObservacoes(),
            obra.getCapaUrl(),
            obra.getStatus(),
            obra.getTipoMaoDeObra(),
            obra.getGrupoDeObra() != null ? GrupoDeObraResponse.from(obra.getGrupoDeObra()) : null,
            obra.getQuantidadeRelatorios(),
            obra.getQuantidadeAtividades(),
            obra.getQuantidadeOcorrencias(),
            obra.getQuantidadeComentarios(),
            relatoriosRecentesProjections.stream()
                    .map(RelatorioResponse::from)
                    .toList()
        );
    }
}
