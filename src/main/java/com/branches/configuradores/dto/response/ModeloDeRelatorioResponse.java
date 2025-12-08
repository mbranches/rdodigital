package com.branches.configuradores.dto.response;

import com.branches.configuradores.domain.ModeloDeRelatorioEntity;
import com.branches.configuradores.domain.enums.RecorrenciaRelatorio;

public record ModeloDeRelatorioResponse(
        Long id,
        String tituloRelatorio,
        RecorrenciaRelatorio recorrenciaRelatorio,
        Boolean showMaoDeObra,
        Boolean showEquipamentos,
        Boolean showAtividades,
        Boolean showOcorrencias,
        Boolean showComentarios,
        Boolean showMateriais,
        Boolean showHorarioDeTrabalho,
        Boolean showFotos,
        Boolean showVideos,
        Boolean isDefault
) {
    public static ModeloDeRelatorioResponse from(ModeloDeRelatorioEntity saved) {
        return new ModeloDeRelatorioResponse(
                saved.getId(),
                saved.getTitulo(),
                saved.getRecorrenciaRelatorio(),
                saved.getShowMaoDeObra(),
                saved.getShowEquipamentos(),
                saved.getShowAtividades(),
                saved.getShowOcorrencias(),
                saved.getShowComentarios(),
                saved.getShowMateriais(),
                saved.getShowHorarioDeTrabalho(),
                saved.getShowFotos(),
                saved.getShowVideos(),
                saved.getIsDefault()
        );
    }
}
