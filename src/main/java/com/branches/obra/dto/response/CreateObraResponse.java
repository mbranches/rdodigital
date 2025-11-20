package com.branches.obra.dto.response;

import com.branches.obra.domain.ObraEntity;
import com.branches.obra.domain.enums.StatusObra;
import com.branches.obra.domain.enums.TipoContratoDeObra;
import com.branches.obra.domain.enums.TipoMaoDeObra;

import java.time.LocalDate;

public record CreateObraResponse(String id,
                                 String nome,
                                 String responsavel,
                                 String contratante,
                                 TipoContratoDeObra tipoContrato,
                                 LocalDate dataInicio,
                                 LocalDate dataPrevistaFim,
                                 String numeroContrato,
                                 String endereco,
                                 String observacoes,
                                 String capaUrl,
                                 StatusObra status,
                                 TipoMaoDeObra tipoMaoDeObra,
                                 GrupoDeObraByCreateObraResponse grupo
) {

    public record GrupoDeObraByCreateObraResponse(
            Long id,
            String descricao
    ) {
        public static GrupoDeObraByCreateObraResponse from(ObraEntity obra) {
            return new GrupoDeObraByCreateObraResponse(
                    obra.getGrupo().getId(),
                    obra.getGrupo().getDescricao()
            );
        }
    }

    public static CreateObraResponse from(ObraEntity obra) {
        GrupoDeObraByCreateObraResponse grupo = obra.getGrupo() != null ? GrupoDeObraByCreateObraResponse.from(obra)
                : null;

        return new CreateObraResponse(
                obra.getIdExterno(),
                obra.getNome(),
                obra.getResponsavel(),
                obra.getContratante(),
                obra.getTipoContrato(),
                obra.getDataInicio(),
                obra.getDataPrevistaFim(),
                obra.getNumeroContrato(),
                obra.getEndereco(),
                obra.getObservacoes(),
                obra.getCapaUrl(),
                obra.getStatus(),
                obra.getTipoMaoDeObra(),
                grupo
        );
    }
}