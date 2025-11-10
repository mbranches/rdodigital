package com.branches.relatorio.maodeobra.service;

import com.branches.exception.BadRequestException;
import com.branches.exception.ForbiddenException;
import com.branches.obra.domain.enums.TipoMaoDeObra;
import com.branches.relatorio.maodeobra.domain.GrupoMaoDeObraEntity;
import com.branches.relatorio.maodeobra.domain.MaoDeObraEntity;
import com.branches.relatorio.maodeobra.dto.request.CreateMaoDeObraRequest;
import com.branches.relatorio.maodeobra.dto.response.CreateMaoDeObraResponse;
import com.branches.relatorio.maodeobra.repository.MaoDeObraRepository;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
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

    public CreateMaoDeObraResponse execute(CreateMaoDeObraRequest request, String tenantExternalId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        checkIfUserCanAccessToMaoDeObra(currentUserTenant);

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
        return MaoDeObraEntity.builder()
                .tenantId(tenantId)
                .nome(request.nome())
                .tipo(request.tipo())
                .horaInicio(request.horaInicio())
                .horasIntervalo(getHorasIntervalo(request))
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

    private LocalTime getHorasIntervalo(CreateMaoDeObraRequest request) {
        if (request.tipo().equals(TipoMaoDeObra.GENERICA)) return null;

        if ((request.horaInicio() != null && request.horaFim() == null) || (request.horaInicio() == null && request.horaFim() != null)) {
            throw new BadRequestException("Quando hora de início ou hora de fim forem preenchidas, ambas devem ser preenchidas");
        }

        if (request.horaInicio() == null) {
            return null;
        }

        int horasDeIntervalo = request.horasIntervalo() != null ? request.horasIntervalo().getHour() : 0;
        int minutosDeIntervalo = request.horasIntervalo() != null ? request.horasIntervalo().getMinute() : 0;

        return request.horaFim().minusHours(request.horaInicio().getHour()).minusMinutes(request.horaInicio().getMinute())
                .minusHours(horasDeIntervalo).minusMinutes(minutosDeIntervalo);
    }

    private void checkIfUserCanAccessToMaoDeObra(UserTenantEntity currentUserTenant) {
        if (!currentUserTenant.getAuthorities().getCadastros().getMaoDeObra()) {
            throw new ForbiddenException();
        }
    }
}
