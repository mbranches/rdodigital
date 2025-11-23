package com.branches.maodeobra.service;

import com.branches.exception.BadRequestException;
import com.branches.obra.domain.enums.TipoMaoDeObra;
import com.branches.maodeobra.domain.GrupoMaoDeObraEntity;
import com.branches.maodeobra.domain.MaoDeObraEntity;
import com.branches.maodeobra.dto.request.CreateMaoDeObraRequest;
import com.branches.maodeobra.dto.response.CreateMaoDeObraResponse;
import com.branches.maodeobra.repository.MaoDeObraRepository;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import com.branches.utils.CalculateHorasTotais;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CreateMaoDeObraService {
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final GetGrupoMaoDeObraByIdAndTenantIdService getGrupoMaoDeObraByIdAndTenantIdService;
    private final MaoDeObraRepository maoDeObraRepository;
    private final CalculateHorasTotais calculateHorasTotais;
    private final CheckIfUserHasAccessToMaoDeObraService checkIfUserHasAccessToMaoDeObraService;

    public CreateMaoDeObraResponse execute(CreateMaoDeObraRequest request, String tenantExternalId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        checkIfUserHasAccessToMaoDeObraService.execute(currentUserTenant);

        TipoMaoDeObra tipo = request.tipo();

        if (tipo.equals(TipoMaoDeObra.PERSONALIZADA) && (request.nome() == null || request.nome().isBlank())) {
            throw new BadRequestException("Nome é obrigatório para mão de obra do tipo PERSONALIZADA");
        }

        MaoDeObraEntity maodeobraEntity = tipo.equals(TipoMaoDeObra.PERSONALIZADA)
                ? buildMaoDeObraEntityPersonalizada(request, tenantId)
                : buildMaoDeObraEntityGenerica(request, tenantId);

        GrupoMaoDeObraEntity grupo = getGrupoMaoDeObraByIdAndTenantIdService.execute(request.grupoId(), tenantId);

        maodeobraEntity.setGrupo(grupo);

        MaoDeObraEntity saved = maoDeObraRepository.save(maodeobraEntity);

        return CreateMaoDeObraResponse.from(saved);
    }

    private MaoDeObraEntity buildMaoDeObraEntityPersonalizada(CreateMaoDeObraRequest request, Long tenantId) {
        LocalTime horaInicio = request.horaInicio();
        LocalTime horaFim = request.horaFim();
        LocalTime horasIntervalo = request.horasIntervalo();

        return MaoDeObraEntity.builder()
                .tenantId(tenantId)
                .nome(request.nome())
                .tipo(request.tipo())
                .horaInicio(horaInicio)
                .horasIntervalo(horasIntervalo)
                .horaFim(horaFim)
                .horasTrabalhadas(calculateHorasTotais.execute(horaInicio, horaFim, horasIntervalo))
                .horaFim(request.horaFim())
                .funcao(request.funcao())
                .build();
    }

    private MaoDeObraEntity buildMaoDeObraEntityGenerica(CreateMaoDeObraRequest request, Long tenantId) {
        return MaoDeObraEntity.builder()
                .tenantId(tenantId)
                .tipo(request.tipo())
                .funcao(request.funcao())
                .build();
    }
}
