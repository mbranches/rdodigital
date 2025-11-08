package com.branches.obra.service;

import com.branches.assinatura.domain.AssinaturaEntity;
import com.branches.assinatura.service.GetAssinaturaActiveByTenantIdService;
import com.branches.obra.domain.GrupoDeObraEntity;
import com.branches.obra.domain.ObraEntity;
import com.branches.obra.dto.request.CreateObraRequest;
import com.branches.obra.dto.response.CreateObraResponse;
import com.branches.obra.repository.ObraRepository;
import com.branches.exception.BadRequestException;
import com.branches.exception.ForbiddenException;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CreateObraService {
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetAssinaturaActiveByTenantIdService getAssinaturaActiveByTenantIdService;
    private final ObraRepository obraRepository;
    private final GetGrupoDeObraByIdAndTenantIdService getGrupoDeObraByIdAndTenantIdService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;

    public CreateObraResponse execute(CreateObraRequest request, String tenantDaObraExternalId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantDaObraExternalId);

        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        verifyIfUserHasPermissionToCreateObra(currentUserTenant);

        verifyIfPlanoAllowsCreateObra(tenantId);

        ObraEntity obraToSave = ObraEntity.builder()
                .nome(request.nome())
                .responsavel(request.responsavel())
                .contratante(request.contratante())
                .tipoContrato(request.tipoContrato())
                .dataInicio(request.dataInicio())
                .dataPrevistaFim(request.dataPrevistaFim())
                .numeroContrato(request.numeroContrato())
                .endereco(request.endereco())
                .observacoes(request.observacoes())
                .tipoMaoDeObra(request.tipoMaoDeObra())
                .status(request.status())
                .ativo(true)
                .build();

        obraToSave.setTenantId(tenantId);

        if (request.grupoId() != null) {
            GrupoDeObraEntity grupo = getGrupoDeObraByIdAndTenantIdService.execute(request.grupoId(), tenantId);

            obraToSave.setGrupo(grupo);
        }

        ObraEntity savedObra = obraRepository.save(obraToSave);

        return CreateObraResponse.from(savedObra);
    }

    private void verifyIfUserHasPermissionToCreateObra(UserTenantEntity currentUserTenant) {
        Boolean userCanCreateOrEdit = currentUserTenant.getAuthorities().getObras().getCanCreateAndEdit();

        if (!userCanCreateOrEdit) {
            throw new ForbiddenException();
        }
    }

    private void verifyIfPlanoAllowsCreateObra(Long tenantId) {
        Integer quantityObrasActive = obraRepository.countByTenantIdAndAtivoIsTrue(tenantId);

        AssinaturaEntity assinaturaAtiva = getAssinaturaActiveByTenantIdService.execute(tenantId);

        if (assinaturaAtiva.getPlano().getLimiteObras() - quantityObrasActive <= 0) {
            throw new BadRequestException("Limite de obras atingido");
        }
    }
}
