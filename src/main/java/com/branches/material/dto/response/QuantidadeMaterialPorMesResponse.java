package com.branches.material.dto.response;

import com.branches.material.repository.projections.QuantidadeMaterialPorMesProjection;
import com.branches.shared.dto.response.TotalPorMesResponse;

import java.util.List;

public record QuantidadeMaterialPorMesResponse(
        Long id,
        String descricao,
        List<TotalPorMesResponse> totalPorMes
) {
    public static QuantidadeMaterialPorMesResponse from(List<QuantidadeMaterialPorMesProjection> projections) {
        if (projections == null || projections.isEmpty()) {
            return null;
        }

        Long materialId = projections.getFirst().getMaterialId();
        String materialDescricao = projections.getFirst().getMaterialDescricao();

        List<TotalPorMesResponse> totalPorMesList = projections.stream()
                .map(proj -> new TotalPorMesResponse(
                        proj.getMes(),
                        proj.getQuantidade()
                ))
                .toList();

        return new QuantidadeMaterialPorMesResponse(
                materialId,
                materialDescricao,
                totalPorMesList
        );
    }
}
