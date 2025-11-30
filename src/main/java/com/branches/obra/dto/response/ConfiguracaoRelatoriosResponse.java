package com.branches.obra.dto.response;

import com.branches.configuradores.domain.enums.RecorrenciaRelatorio;
import com.branches.obra.domain.ConfiguracaoRelatoriosEntity;

public record ConfiguracaoRelatoriosResponse(RecorrenciaRelatorio recorrenciaRelatorio,
                                             Boolean showCondicaoClimatica,
                                             Boolean showMaoDeObra,
                                             Boolean showEquipamentos,
                                             Boolean showAtividades,
                                             Boolean showOcorrencias,
                                             Boolean showComentarios,
                                             Boolean showMateriais,
                                             Boolean showHorarioDeTrabalho,
                                             Boolean showFotos,
                                             Boolean showVideos) {
    public static ConfiguracaoRelatoriosResponse from(ConfiguracaoRelatoriosEntity configuracaoRelatorios) {
        return new ConfiguracaoRelatoriosResponse(
                configuracaoRelatorios.getRecorrenciaRelatorio(),
                configuracaoRelatorios.getShowCondicaoClimatica(),
                configuracaoRelatorios.getShowMaoDeObra(),
                configuracaoRelatorios.getShowEquipamentos(),
                configuracaoRelatorios.getShowAtividades(),
                configuracaoRelatorios.getShowOcorrencias(),
                configuracaoRelatorios.getShowComentarios(),
                configuracaoRelatorios.getShowMateriais(),
                configuracaoRelatorios.getShowHorarioDeTrabalho(),
                configuracaoRelatorios.getShowFotos(),
                configuracaoRelatorios.getShowVideos()
        );
    }
}
