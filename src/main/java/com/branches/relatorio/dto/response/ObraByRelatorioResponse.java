package com.branches.relatorio.dto.response;

public record ObraByRelatorioResponse(
        String id,
        String nome,
        String endereco,
        String contratante,
        String responsavel,
        String numeroContrato
) {
}
