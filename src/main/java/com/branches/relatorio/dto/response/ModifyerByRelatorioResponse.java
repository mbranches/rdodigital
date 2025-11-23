package com.branches.relatorio.dto.response;

import java.time.LocalDateTime;

public record ModifyerByRelatorioResponse(
        String nome,
        LocalDateTime dataHora
) {
}
