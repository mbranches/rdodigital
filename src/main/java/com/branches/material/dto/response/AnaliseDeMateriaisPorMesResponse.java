package com.branches.material.dto.response;

import com.branches.shared.dto.response.TotalPorMesResponse;

import java.util.List;

public record AnaliseDeMateriaisPorMesResponse(
        List<TotalPorMesResponse> totalPorMes,
        List<QuantidadeMaterialPorMesResponse> quantidadeDeMaterialPorMes
) {
}
