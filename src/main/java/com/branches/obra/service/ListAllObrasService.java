package com.branches.obra.service;

import com.branches.obra.dto.response.ObraByListAllResponse;
import com.branches.obra.repository.ObraRepository;
import com.branches.obra.repository.projections.ObraProjection;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.domain.enums.PerfilUserTenant;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.branches.usertenant.domain.enums.PerfilUserTenant.ADMINISTRADOR;

@Service
@RequiredArgsConstructor
public class ListAllObrasService {
    private final ObraRepository obraRepository;
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;

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

                    long prazoTotal = Math.max(ChronoUnit.DAYS.between(startDate, endDate), 1);

                    LocalDate dataFimToReferencia = o.getDataFimReal() != null ? o.getDataFimReal() : LocalDate.now();
                    long prazoDecorrido = ChronoUnit.DAYS.between(startDate, dataFimToReferencia);
                    BigDecimal prazoPercentualDecorrido = prazoTotal - prazoDecorrido <= 0 ? BigDecimal.valueOf(100)
                            : BigDecimal.valueOf(prazoDecorrido)
                            .multiply(BigDecimal.valueOf(100))
                            .divide(BigDecimal.valueOf(prazoTotal), 2, RoundingMode.HALF_UP);

                    return ObraByListAllResponse.from(o, prazoPercentualDecorrido);

                })
                .toList();
    }
}
