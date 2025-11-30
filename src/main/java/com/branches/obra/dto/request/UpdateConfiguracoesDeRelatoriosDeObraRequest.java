package com.branches.obra.dto.request;

import com.branches.configuradores.domain.enums.RecorrenciaRelatorio;
import jakarta.validation.constraints.NotNull;

public record UpdateConfiguracoesDeRelatoriosDeObraRequest(
        @NotNull(message = "O campo 'recorrenciaRelatorio' é obrigatório")
        RecorrenciaRelatorio recorrenciaRelatorio,
        @NotNull(message = "O campo 'showCondicaoClimatica' é obrigatório")
        Boolean showCondicaoClimatica,
        @NotNull(message = "O campo 'showMaoDeObra' é obrigatório")
        Boolean showMaoDeObra,
        @NotNull(message = "O campo 'showEquipamentos' é obrigatório")
        Boolean showEquipamentos,
        @NotNull(message = "O campo 'showAtividades' é obrigatório")
        Boolean showAtividades,
        @NotNull(message = "O campo 'showOcorrencias' é obrigatório")
        Boolean showOcorrencias,
        @NotNull(message = "O campo 'showComentarios' é obrigatório")
        Boolean showComentarios,
        @NotNull(message = "O campo 'showMateriais' é obrigatório")
        Boolean showMateriais,
        @NotNull(message = "O campo 'showHorarioDeTrabalho' é obrigatório")
        Boolean showHorarioDeTrabalho,
        @NotNull(message = "O campo 'showFotos' é obrigatório")
        Boolean showFotos,
        @NotNull(message = "O campo 'showVideos' é obrigatório")
        Boolean showVideos) {
}