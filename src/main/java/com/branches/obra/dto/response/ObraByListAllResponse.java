package com.branches.obra.dto.response;

import com.branches.obra.domain.enums.StatusObra;
import com.branches.obra.repository.projections.ObraProjection;

public record ObraByListAllResponse(
        String id,
        String nome,
        StatusObra status,
        String capaUrl,
        Long quantityOfRelatorios
) {
    public static ObraByListAllResponse from(ObraProjection obra) {
        return new ObraByListAllResponse(
                obra.getIdExterno(),
                obra.getNome(),
                obra.getStatus(),
                obra.getCapaUrl(),
                obra.getQuantityOfRelatorios()
        );
    }
}
