package com.branches.obra.service;

import com.branches.domain.GrupoDeObraEntity;
import com.branches.exception.ForbiddenException;
import com.branches.obra.domain.ObraEntity;
import com.branches.obra.dto.request.UpdateObraRequest;
import com.branches.obra.repository.ObraRepository;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UpdateObraService {
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final ObraRepository obraRepository;
    private final GetGrupoDeObraByIdAndTenantIdService getGrupoDeObraByIdAndTenantIdService;
    private final GetObraByIdExternoAndTenantIdService getObraByIdExternoAndTenantIdService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;

    public void execute(UpdateObraRequest request, String obraExternalId, String tenantExternalId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        ObraEntity obra = getObraByIdExternoAndTenantIdService.execute(obraExternalId, tenantId);

        checkIfUserCanUpdateObra(obra.getId(), currentUserTenant);

        obra.setNome(request.nome());
        obra.setResponsavel(request.responsavel());
        obra.setContratante(request.contratante());
        obra.setTipoContrato(request.tipoContrato());
        obra.setDataInicio(request.dataInicio());
        obra.setDataPrevistaFim(request.dataPrevistaFim());
        obra.setNumeroContrato(request.numeroContrato());
        obra.setEndereco(request.endereco());
        obra.setObservacoes(request.observacoes());
        obra.setTipoMaoDeObra(request.tipoMaoDeObra());
        obra.setStatus(request.status());

        GrupoDeObraEntity grupo = getGrupoDeObraByIdAndTenantIdService.execute(request.grupoId(), tenantId);

        obra.setGrupo(grupo);

        obraRepository.save(obra);
    }

    private void checkIfUserCanUpdateObra(Long id, UserTenantEntity userTenant) {
        if (!userTenant.getAuthorities().getObras().getCanCreateAndEdit() || !userTenant.getObrasPermitidasIds().contains(id)) {
            throw new ForbiddenException();
        }
    }
}
