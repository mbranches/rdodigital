package com.branches.relatorio.maodeobra.service;

import com.branches.exception.BadRequestException;
import com.branches.obra.domain.enums.TipoMaoDeObra;
import com.branches.relatorio.maodeobra.domain.GrupoMaoDeObraEntity;
import com.branches.relatorio.maodeobra.domain.MaoDeObraEntity;
import com.branches.relatorio.maodeobra.dto.request.UpdateMaoDeObraRequest;
import com.branches.relatorio.maodeobra.repository.MaoDeObraRepository;
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
public class UpdateMaoDeObraService {
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final GetMaoDeObraByIdAndTenantIdService getMaoDeObraByIdService;
    private final GetGrupoMaoDeObraByIdAndTenantIdService getGrupoMaoDeObraByIdAndTenantIdService;
    private final CalculateHorasTotais calculateHorasTotais;
    private final MaoDeObraRepository maoDeObraRepository;
    private final CheckIfUserHasAccessToMaoDeObraService checkIfUserHasAccessToMaoDeObraService;

    public void execute(UpdateMaoDeObraRequest request, Long id, String tenantExternalId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        checkIfUserHasAccessToMaoDeObraService.execute(currentUserTenant);

        MaoDeObraEntity maoDeObraEntity = getMaoDeObraByIdService.execute(id, tenantId);

        TipoMaoDeObra tipo = maoDeObraEntity.getTipo();

        GrupoMaoDeObraEntity grupo = getGrupoMaoDeObraByIdAndTenantIdService.execute(request.grupoId(), tenantId);

        if (tipo.equals(TipoMaoDeObra.PERSONALIZADA) && (request.nome() == null || request.nome().isBlank())) {
            throw new BadRequestException("Nome é obrigatório para mão de obra do tipo PERSONALIZADA");
        }

        maoDeObraEntity.setFuncao(request.funcao());
        maoDeObraEntity.setGrupo(grupo);

        if (tipo.equals(TipoMaoDeObra.PERSONALIZADA)) {
            LocalTime horaInicio = request.horaInicio();
            LocalTime horaFim = request.horaFim();
            LocalTime horasIntervalo = request.horasIntervalo();

            maoDeObraEntity.setNome(request.nome());
            maoDeObraEntity.setHoraInicio(horaInicio);
            maoDeObraEntity.setHoraFim(horaFim);
            maoDeObraEntity.setHorasIntervalo(horasIntervalo);
            maoDeObraEntity.setHorasTrabalhadas(calculateHorasTotais.execute(horaInicio, horaFim, horasIntervalo));
        }

        maoDeObraRepository.save(maoDeObraEntity);
    }
}
