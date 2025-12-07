package com.branches.atividade.dto.request;

import com.branches.maodeobra.dto.request.UpdateMaoDeObraDeAtividadeRequest;
import com.branches.atividade.domain.enums.StatusAtividade;
import com.branches.relatorio.dto.request.CampoPersonalizadoRequest;
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
        List<UpdateMaoDeObraDeAtividadeRequest> maoDeObra,
        @Valid
        List<CampoPersonalizadoRequest> camposPersonalizados
) {
}
