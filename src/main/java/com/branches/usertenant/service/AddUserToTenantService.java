package com.branches.usertenant.service;

import com.branches.assinatura.domain.AssinaturaEntity;
import com.branches.assinatura.service.GetAssinaturaActiveByTenantIdService;
import com.branches.exception.BadRequestException;
import com.branches.exception.ForbiddenException;
import com.branches.obra.domain.ObraEntity;
import com.branches.obra.service.GetObrasByTenantIdAndIdExternoIn;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.user.domain.UserEntity;
import com.branches.user.service.GetUserByIdExternoService;
import com.branches.usertenant.domain.Authorities;
import com.branches.usertenant.domain.UserObraPermitidaEntity;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.domain.UserTenantKey;
import com.branches.usertenant.domain.enums.PerfilUserTenant;
import com.branches.usertenant.dto.request.AddUserToTenantRequest;
import com.branches.usertenant.repository.UserTenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.branches.usertenant.domain.enums.PerfilUserTenant.ADMINISTRADOR;

@Transactional
@RequiredArgsConstructor
@Service
public class AddUserToTenantService {
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final GetAssinaturaActiveByTenantIdService getAssinaturaActiveByTenantIdService;
    private final UserTenantRepository userTenantRepository;
    private final GetUserByIdExternoService getUserByIdExternoService;
    private final GetObrasByTenantIdAndIdExternoIn getObrasByTenantIdAndIdExternoIn;

    public void execute(AddUserToTenantRequest request, String tenantExternalId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        checkIfAddUserIsAllowed(currentUserTenant);

        PerfilUserTenant perfil = request.perfil();

        boolean isAdminPerfil = perfil.equals(ADMINISTRADOR);

        if (!isAdminPerfil && request.authorities() == null) {
            throw new BadRequestException("As authorities devem ser fornecidas para perfis diferentes de ADMINISTRADOR.");
        }

        UserEntity user = getUserByIdExternoService.execute(request.userId());

        checkIfUserAlreadyInTenant(user.getId(), tenantId);

        UserTenantEntity newUserTenant = UserTenantEntity.builder()
                .user(user)
                .tenantId(tenantId)
                .perfil(perfil)
                .authorities(isAdminPerfil ? Authorities.adminAuthorities() : request.authorities())
                .ativo(true)
                .build();

        List<ObraEntity> obras = getObrasByTenantIdAndIdExternoIn.execute(tenantId, request.obrasIds());
        Set<UserObraPermitidaEntity> userObrasPermitidaEntities = getUserObrasPermitidaEntities(newUserTenant, obras);

        newUserTenant.setUserObraPermitidaEntities(userObrasPermitidaEntities);
        newUserTenant.setarId();

        userTenantRepository.save(newUserTenant);
    }

    private void checkIfUserAlreadyInTenant(Long userId, Long tenantId) {
        boolean userAlreadyInTenant = userTenantRepository.existsById(UserTenantKey.from(userId, tenantId));

        if (!userAlreadyInTenant) return;

        throw new BadRequestException("Usuário já pertence a este tenant");
    }

    private Set<UserObraPermitidaEntity> getUserObrasPermitidaEntities(UserTenantEntity newUserTenant, List<ObraEntity> obras) {
        List<Long> obrasIds = obras.stream().map(ObraEntity::getId).toList();

        return obrasIds.stream()
                .map(obraId -> UserObraPermitidaEntity.builder()
                        .userTenant(newUserTenant)
                        .obraId(obraId)
                        .build())
                .collect(Collectors.toSet());
    }

    private void checkIfAddUserIsAllowed(UserTenantEntity currentUserTenant) {
        if (!currentUserTenant.getPerfil().equals(ADMINISTRADOR)) throw new ForbiddenException();

        long quantityOfUsers = userTenantRepository.countByTenantIdAndAtivoIsTrue(currentUserTenant.getTenantId());

        AssinaturaEntity activeAssinatura = getAssinaturaActiveByTenantIdService.execute(currentUserTenant.getTenantId());

        if (quantityOfUsers >= activeAssinatura.getPlano().getLimiteUsuarios()) {
            throw new BadRequestException("Limite de usuários atingido para o plano atual.");
        }
    }
}
