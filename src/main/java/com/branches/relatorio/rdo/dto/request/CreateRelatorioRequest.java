package com.branches.relatorio.rdo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateRelatorioRequest(
        @NotBlank(message = "O campo 'obraId' é obrigatório")
        String obraId,
        @NotNull(message = "O campo 'data' é obrigatório")
        LocalDate data,
        @NotNull(message = "O campo 'copiarInformacoesDoUltimoRelatorio' é obrigatório")
        Boolean copiarInformacoesDoUltimoRelatorio,
        @NotNull(message = "O campo 'copiarCondicoesClimaticas' é obrigatório")
        Boolean copiarCondicoesClimaticas,
        @NotNull(message = "O campo 'copiarMaoDeObra' é obrigatório")
        Boolean copiarMaoDeObra,
        @NotNull(message = "O campo 'copiarEquipamentos' é obrigatório")
        Boolean copiarEquipamentos,
        @NotNull(message = "O campo 'copiarAtividades' é obrigatório")
        Boolean copiarAtividades,
        @NotNull(message = "O campo 'copiarOcorrencias' é obrigatório")
        Boolean copiarOcorrencias,
        @NotNull(message = "O campo 'copiarComentarios' é obrigatório")
        Boolean copiarComentarios
) {
}
