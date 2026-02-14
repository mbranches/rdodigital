package com.branches.maodeobra.service;

import com.branches.maodeobra.dto.response.AnaliseDeMaoDeObraPorMesResponse;
import com.branches.maodeobra.dto.response.AnaliseMaoDeObraPorMesResponse;
import com.branches.maodeobra.repository.MaoDeObraRepository;
import com.branches.maodeobra.repository.projections.AnaliseMaoDeObraPorMesProjection;
import com.branches.maodeobra.repository.projections.TotalHoraDeMaoDeObraPorMesProjection;
import com.branches.obra.service.CheckIfUserHasAccessToObraService;
import com.branches.obra.domain.ObraEntity;
import com.branches.obra.service.GetObraByIdExternoAndTenantIdService;
import com.branches.shared.dto.response.TotalDecimalPorMesResponse;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class GetAnaliseMaoDeObraPorMesService {

    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final GetObraByIdExternoAndTenantIdService getObraByIdExternoAndTenantIdService;
    private final CheckIfConfiguracaoDeRelatorioDaObraPermiteMaoDeObraService checkIfConfiguracaoDeRelatorioDaObraPermiteMaoDeObraService;
    private final CheckIfUserHasAccessToObraService checkIfUserHasAccessToObraService;
    private final CheckIfUserCanViewMaoDeObraService checkIfUserCanViewMaoDeObraService;
    private final MaoDeObraRepository maoDeObraRepository;

    public AnaliseDeMaoDeObraPorMesResponse execute(String tenantExternalId, Integer year, String obraExternalId, Long maoDeObraId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        if (obraExternalId != null) {
            ObraEntity obra = getObraByIdExternoAndTenantIdService.execute(obraExternalId, tenantId);
            checkIfUserHasAccessToObraService.execute(currentUserTenant, obra.getId());
            checkIfConfiguracaoDeRelatorioDaObraPermiteMaoDeObraService.execute(obra);
        }

        checkIfUserCanViewMaoDeObraService.execute(currentUserTenant);

        List<TotalHoraDeMaoDeObraPorMesProjection> totalDeMaoDeObraPorMesProjectionList = maoDeObraRepository.findTotalHoraDeMaoDeObraPorMes(tenantId, year, obraExternalId, maoDeObraId);
        List<AnaliseMaoDeObraPorMesProjection> quantidadeMaoDeObraPorMesProjectionList = maoDeObraRepository.findQuantidadeMaoDeObraPorMes(tenantId, year, obraExternalId);

        Map<Integer, BigDecimal> mapMesAndTotalHoras = totalDeMaoDeObraPorMesProjectionList.stream()
                .collect(Collectors.toMap(
                        TotalHoraDeMaoDeObraPorMesProjection::getMes,
                        projection -> BigDecimal.valueOf(projection.getTotalMinutos()).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP
                )));

        Map<Long, List<AnaliseMaoDeObraPorMesProjection>> mapMaoDeObraIdToQuantidadePorMes = quantidadeMaoDeObraPorMesProjectionList.stream()
                .collect(Collectors.groupingBy(AnaliseMaoDeObraPorMesProjection::getMaoDeObraId));

        List<TotalDecimalPorMesResponse> totalDeMaoDeObraPorMes = Arrays.stream(Month.values())
                .map(Month::getValue)
                .map(mesInt -> {
                    BigDecimal total = mapMesAndTotalHoras.getOrDefault(mesInt, BigDecimal.ZERO);

                    return new TotalDecimalPorMesResponse(mesInt, total);
                })
                .toList();


        List<AnaliseMaoDeObraPorMesResponse> quantidadeMaoDeObraPorMesResponses = mapMaoDeObraIdToQuantidadePorMes.values().stream()
                .map(AnaliseMaoDeObraPorMesResponse::from)
                .toList();

        return new AnaliseDeMaoDeObraPorMesResponse(totalDeMaoDeObraPorMes, quantidadeMaoDeObraPorMesResponses);
    }
}
