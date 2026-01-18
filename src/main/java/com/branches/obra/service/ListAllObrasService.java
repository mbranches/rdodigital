package com.branches.obra.service;

import com.branches.obra.dto.response.ObraByListAllResponse;
import com.branches.obra.repository.ObraRepository;
import com.branches.obra.repository.projections.ObraProjection;
import com.branches.shared.calculators.CalculatePrazoDecorrido;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.domain.enums.PerfilUserTenant;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static com.branches.usertenant.domain.enums.PerfilUserTenant.ADMINISTRADOR;

@Service
@RequiredArgsConstructor
public class ListAllObrasService {
    private final ObraRepository obraRepository;
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final CalculatePrazoDecorrido calculatePrazoDecorrido;

    public List<ObraByListAllResponse> execute(String tenantExternalId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        PerfilUserTenant userPerfil = currentUserTenant.getPerfil();
        List<Long> userAllowedObrasIds = currentUserTenant.getObrasPermitidasIds();

        List<ObraProjection> obras = userPerfil.equals(ADMINISTRADOR) ? obraRepository.findAllByTenantIdProjection(tenantId)
                : obraRepository.findAllByTenantIdAndIdInProjection(tenantId, userAllowedObrasIds);

        return obras.stream()
                .map(o -> {
                    LocalDate startDate = o.getDataInicio();
                    LocalDate endDate = o.getDataPrevistaFim();

                    LocalDate dataFimToReferencia = o.getDataFimReal() != null ? o.getDataFimReal() : LocalDate.now();
                    BigDecimal prazoPercentualDecorrido = calculatePrazoDecorrido.execute(startDate, endDate, dataFimToReferencia);

                    return ObraByListAllResponse.from(o, prazoPercentualDecorrido);

                })
                .toList();
    }
}
