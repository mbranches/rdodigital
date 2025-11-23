package com.branches.relatorio.dto.request;

import com.branches.relatorio.domain.enums.StatusAtividade;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

public record UpdateAtividadeDeRelatorioRequest(
        @NotBlank(message = "O campo 'descricao' é obrigatório")
        String descricao,
        Integer quantidadeRealizada,
        String unidadeMedida,
        @DecimalMax(value = "100.00", message = "O campo 'porcentagemConcluida' deve ser no máximo 100.00")
        @DecimalMin(value = "0.00", message = "O campo 'porcentagemConcluida' deve ser no mínimo 0.00")
        BigDecimal porcentagemConcluida,
        @NotNull(message = "O campo 'status' é obrigatório")
        StatusAtividade status,
        LocalTime horaInicio,
        LocalTime horaFim,
        @Valid
        List<MaoDeObraDeAtividadeRequest> maoDeObra,
        @Valid
        List<CampoPersonalizadoRequest> camposPersonalizados
        //todo: adicionar fotos quando for implementado
) {
}
