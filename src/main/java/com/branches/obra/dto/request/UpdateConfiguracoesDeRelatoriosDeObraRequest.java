package com.branches.obra.dto.request;

import com.branches.configuradores.domain.enums.RecorrenciaRelatorio;

public record UpdateConfiguracoesDeRelatoriosDeObraRequest(RecorrenciaRelatorio recorrenciaRelatorio,
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
}