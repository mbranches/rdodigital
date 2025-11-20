package com.branches.relatorio.rdo.dto.response;

public record ObraByRelatorioResponse(
        String id,
        String nome,
        String endereco,
        String contratante,
        String responsavel,
        String numeroContrato
) {
}
