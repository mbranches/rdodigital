package com.branches.material.service;

import com.branches.material.dto.response.AnaliseDeMateriaisPorMesResponse;
import com.branches.material.dto.response.QuantidadeMaterialPorMesResponse;
import com.branches.material.repository.MaterialRepository;
import com.branches.material.repository.projections.QuantidadeMaterialPorMesProjection;
import com.branches.material.repository.projections.TotalDeMaterialPorMesProjection;
import com.branches.obra.service.CheckIfUserHasAccessToObraService;
import com.branches.obra.domain.ObraEntity;
import com.branches.obra.service.GetObraByIdExternoAndTenantIdService;
import com.branches.shared.dto.response.TotalPorMesResponse;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class GetAnaliseMateriaisPorMesService {

    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final GetObraByIdExternoAndTenantIdService getObraByIdExternoAndTenantIdService;
    private final CheckIfConfiguracaoDeRelatorioDaObraPermiteMaterialService checkIfConfiguracaoDeRelatorioDaObraPermiteMaterialService;
    private final CheckIfUserHasAccessToObraService checkIfUserHasAccessToObraService;
    private final CheckIfUserCanViewMateriaisDeRelatorioService checkIfUserCanViewMateriaisDeRelatorioService;
    private final MaterialRepository materialRepository;

    public AnaliseDeMateriaisPorMesResponse execute(String tenantExternalId, Integer year, String obraExternalId, Long materialId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        if (obraExternalId != null) {
            ObraEntity obra = getObraByIdExternoAndTenantIdService.execute(obraExternalId, tenantId);
            checkIfUserHasAccessToObraService.execute(currentUserTenant, obra.getId());
            checkIfConfiguracaoDeRelatorioDaObraPermiteMaterialService.execute(obra);
        }

        checkIfUserCanViewMateriaisDeRelatorioService.execute(currentUserTenant);

        List<TotalDeMaterialPorMesProjection> totalDeMaterialPorMesProjectionList = materialRepository.findTotalMaterialPorMes(tenantId, year, obraExternalId, materialId);
        List<QuantidadeMaterialPorMesProjection> quantidadeMaterialPorMesProjectionList = materialRepository.findQuantidadeMaterialPorMes(tenantId, year, obraExternalId);

        Map<Integer, Long> mapMesAndQuantidade = totalDeMaterialPorMesProjectionList.stream()
                .collect(Collectors.toMap(
                        TotalDeMaterialPorMesProjection::getMes,
                        TotalDeMaterialPorMesProjection::getQuantidade
                ));

        Map<Long, List<QuantidadeMaterialPorMesProjection>> mapMaterialIdToQuantidadePorMes = quantidadeMaterialPorMesProjectionList.stream()
                .collect(Collectors.groupingBy(QuantidadeMaterialPorMesProjection::getMaterialId));

        List<TotalPorMesResponse> totalDeMateriaisPorMes = Arrays.stream(Month.values())
                .map(Month::getValue)
                .map(mesInt -> {
                    Long quantidade = mapMesAndQuantidade.getOrDefault(mesInt, 0L);

                    return new TotalPorMesResponse(mesInt, quantidade);
                })
                .toList();


        List<QuantidadeMaterialPorMesResponse> quantidadeMaterialPorMesResponses = mapMaterialIdToQuantidadePorMes.values().stream()
                .map(QuantidadeMaterialPorMesResponse::from)
                .toList();

        return new AnaliseDeMateriaisPorMesResponse(totalDeMateriaisPorMes, quantidadeMaterialPorMesResponses);
    }
}
