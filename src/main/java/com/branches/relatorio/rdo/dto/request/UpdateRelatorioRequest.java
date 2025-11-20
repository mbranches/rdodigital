package com.branches.relatorio.rdo.dto.request;

import com.branches.relatorio.rdo.domain.enums.StatusRelatorio;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record UpdateRelatorioRequest(
        @NotNull(message = "O campo 'numeroRelatorio' é obrigatório")
        @Min(value = 1, message = "O campo 'numeroRelatorio' deve ser maior que zero")
        Long numeroRelatorio,

        @NotNull(message = "O campo 'data' é obrigatório")
        LocalDate data,

        @NotNull(message = "O campo 'prazoContratual' é obrigatório")
        Long prazoContratual,
        @NotNull(message = "O campo 'prazoDecorrido' é obrigatório")
        Long prazoDecorrido,
        @NotNull(message = "O campo 'prazoPraVencer' é obrigatório")
        Long prazoPraVencer,

        @Valid
        @NotNull(message = "O campo 'caracteristicasManha' é obrigatório")
        CaracteristicaDePeriodoDoDiaRequest caracteristicasManha,
        @Valid
        @NotNull(message = "O campo 'caracteristicasTarde' é obrigatório")
        CaracteristicaDePeriodoDoDiaRequest caracteristicasTarde,
        @Valid
        @NotNull(message = "O campo 'caracteristicasNoite' é obrigatório")
        CaracteristicaDePeriodoDoDiaRequest caracteristicasNoite,
        @DecimalMax(value = "999.99", message = "O campo 'indicePluviometrico' deve ser menor ou igual a 999.99")
        @DecimalMin(value = "0.00", message = "O campo 'indicePluviometrico' deve ser maior ou igual a 0.00")
        BigDecimal indicePluviometrico,

        @Valid
        List<MaoDeObraDeRelatorioRequest> maoDeObra,

        @Valid
        List<EquipamentoDeRelatorioRequest> equipamentos,

        @Valid
        List<AtividadeDeRelatorioRequest> atividades,

        @Valid
        List<OcorrenciaDeRelatorioRequest> ocorrencias,

        @Valid
        List<ComentarioDeRelatorioRequest> comentarios,

        //todo: adicionar campo de fotos quando for implementado
        @NotNull(message = "O campo 'status' é obrigatório")
        StatusRelatorio status
) {
}
