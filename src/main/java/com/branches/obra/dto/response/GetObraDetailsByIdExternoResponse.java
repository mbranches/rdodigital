package com.branches.obra.dto.response;

import com.branches.arquivo.domain.ArquivoEntity;
import com.branches.arquivo.dto.response.ArquivoResponse;
import com.branches.obra.domain.enums.StatusObra;
import com.branches.obra.domain.enums.TipoContratoDeObra;
import com.branches.obra.domain.enums.TipoMaoDeObra;
import com.branches.obra.repository.projections.ObraDetailsProjection;
import com.branches.relatorio.dto.response.RelatorioResponse;
import com.branches.relatorio.repository.projections.RelatorioProjection;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record GetObraDetailsByIdExternoResponse(
        String idExterno,
        String nome,
        String responsavel,
        String contratante,
        TipoContratoDeObra tipoContrato,
        LocalDate dataInicio,
        LocalDate dataPrevistaFim,
        LocalDate dataConclusao,
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
        Long quantidadeFotos,
        List<RelatorioResponse> relatoriosRecentes,
        List<ArquivoResponse> fotosRecentes
) {
    public static GetObraDetailsByIdExternoResponse from(ObraDetailsProjection obra, List<RelatorioProjection> relatoriosRecentesProjections, List<ArquivoEntity> fotosRecentes, long prazoContratual, Long prazoPraVencer, BigDecimal porcentagemPrazoDecorrido, long prazoDecorrido) {
        return new GetObraDetailsByIdExternoResponse(
            obra.getIdExterno(),
            obra.getNome(),
            obra.getResponsavel(),
            obra.getContratante(),
            obra.getTipoContrato(),
            obra.getDataInicio(),
            obra.getDataPrevistaFim(),
            obra.getDataFimReal(),
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
            obra.getQuantidadeFotos(),
            relatoriosRecentesProjections.stream()
                    .map(RelatorioResponse::from)
                    .toList(),
            fotosRecentes.stream()
                    .map(ArquivoResponse::from)
                    .toList()
        );
    }
}
