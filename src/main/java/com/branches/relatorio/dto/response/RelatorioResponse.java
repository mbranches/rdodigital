package com.branches.relatorio.dto.response;

import com.branches.obra.dto.response.ObraByRelatorioResponse;
import com.branches.relatorio.domain.enums.StatusRelatorio;
import com.branches.relatorio.repository.projections.RelatorioProjection;

import java.time.LocalDate;

public record RelatorioResponse(
        String id,
        LocalDate data,
        Long numero,
        StatusRelatorio status,
        Long quantidadeFotos,
        String pdfUrl,
        ObraByRelatorioResponse obra
) {
    public static RelatorioResponse from(RelatorioProjection relatorio) {
        ObraByRelatorioResponse obra = new ObraByRelatorioResponse(relatorio.getObraIdExterno(), relatorio.getObraNome(), relatorio.getObraEndereco(), relatorio.getObraContratante(), relatorio.getObraResponsavel(), relatorio.getObraNumeroContrato());

        return new RelatorioResponse(
                relatorio.getIdExterno(),
                relatorio.getDataInicio(),
                relatorio.getNumero(),
                relatorio.getStatus(),
                relatorio.getQuantidadeFotos(),
                relatorio.getPdfUrl(),
                obra
        );
    }
}
