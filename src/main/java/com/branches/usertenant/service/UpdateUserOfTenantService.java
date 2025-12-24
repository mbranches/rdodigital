package com.branches.usertenant.service;

import com.branches.exception.BadRequestException;
import com.branches.exception.ForbiddenException;
import com.branches.obra.domain.ObraEntity;
import com.branches.obra.service.GetObrasByTenantIdAndIdExternoIn;
import com.branches.tenant.domain.TenantEntity;
import com.branches.tenant.service.GetTenantByIdExternoService;
import com.branches.user.domain.UserEntity;
import com.branches.user.service.GetUserByIdExternoService;
import com.branches.usertenant.domain.Authorities;
import com.branches.usertenant.domain.UserObraPermitidaEntity;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.domain.UserTenantKey;
import com.branches.usertenant.domain.enums.PerfilUserTenant;
import com.branches.usertenant.dto.request.UpdateUserOfTenantRequest;
import com.branches.usertenant.repository.UserTenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UpdateUserOfTenantService {
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final GetUserByIdExternoService getUserByIdExternoService;
    private final GetUserTenantByIdService getUserTenantByIdService;
    private final GetObrasByTenantIdAndIdExternoIn getObrasByTenantIdAndIdExternoIn;
    private final UserTenantRepository userTenantRepository;
    private final GetTenantByIdExternoService getTenantByIdExternoService;

    public void execute(UpdateUserOfTenantRequest request, String tenantExternalId, String userExternalId, List<UserTenantEntity> userTenants) {
        TenantEntity tenant = getTenantByIdExternoService.execute(tenantExternalId);
        Long tenantId = tenant.getId();

        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        checkIfUserCanUpdate(currentUserTenant);

        UserEntity user = getUserByIdExternoService.execute(userExternalId);

        boolean isPerfilAdmin = request.perfil().equals(PerfilUserTenant.ADMINISTRADOR);

        if (!isPerfilAdmin && request.authorities() == null) {
            throw new BadRequestException("As authorities devem ser fornecidas para perfis diferentes de ADMINISTRADOR.");
        }

        UserTenantEntity userTenantToEdit = getUserTenantByIdService.execute(UserTenantKey.from(user.getId(), tenantId));

        if (tenant.getUserResponsavelId().equals(user.getId())) {
            if (!request.ativo()) {
                throw new BadRequestException("Não é possível desativar o usuário responsável pelo tenant.");
            }
            if (!request.perfil().equals(PerfilUserTenant.ADMINISTRADOR)) {
                throw new BadRequestException("O usuário responsável pelo tenant deve ter o perfil de ADMINISTRADOR.");
            }
        }

        userTenantToEdit.setPerfil(request.perfil());
        userTenantToEdit.setAuthorities(isPerfilAdmin ? Authorities.adminAuthorities() : request.authorities());
        userTenantToEdit.setAtivo(request.ativo());
        userTenantToEdit.setCargo(request.cargo());

        List<ObraEntity> obrasAllowed = getObrasByTenantIdAndIdExternoIn.execute(tenantId, request.obrasPermitidasIds());

        Set<UserObraPermitidaEntity> userObrasPermitidaEntities = getUserObrasPermitidaEntities(userTenantToEdit, obrasAllowed);
        userTenantToEdit.setUserObraPermitidaEntities(userObrasPermitidaEntities);

        userTenantRepository.save(userTenantToEdit);
    }

    private Set<UserObraPermitidaEntity> getUserObrasPermitidaEntities(UserTenantEntity userTenantToEdit, List<ObraEntity> obras) {
        List<Long> obrasIds = obras.stream().map(ObraEntity::getId).toList();

        return obrasIds.stream()
                .map(obraId -> {
                    UserObraPermitidaEntity obraPermitidaEntity = UserObraPermitidaEntity.builder()
                            .userTenant(userTenantToEdit)
                            .obraId(obraId)
                            .build();
                    obraPermitidaEntity.setarId();

                    return obraPermitidaEntity;
                })
                .collect(Collectors.toSet());
    }

    private void checkIfUserCanUpdate(UserTenantEntity currentUserTenant) {
        if (currentUserTenant.getPerfil().equals(PerfilUserTenant.ADMINISTRADOR)) return;

        throw new ForbiddenException();
    }
}
