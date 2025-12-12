package com.branches.obra.service;

import com.branches.exception.BadRequestException;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserObraPermitidaEntity;
import com.branches.usertenant.domain.UserObraPermitidaKey;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.repository.UserObraPermitidaRepository;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import com.branches.usertenant.service.GetUserTenantByUserIdExternoAndTenantIdService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class RemoveUserFromObraService {
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetObraIdByIdExternoService getObraIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final CheckIfUserCanEditObraService checkIfUserCanEditObraService;
    private final GetUserTenantByUserIdExternoAndTenantIdService getUserTenantByUserIdExternoAndTenantIdService;
    private final UserObraPermitidaRepository userObraPermitidaRepository;

    public void execute(String userId, String obraExternalId, String tenantExternalId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        Long obraId = getObraIdByIdExternoService.execute(obraExternalId, tenantId);

        checkIfUserCanEditObraService.execute(currentUserTenant, obraId);

        UserTenantEntity userTenantToRemove = getUserTenantByUserIdExternoAndTenantIdService.execute(userId, tenantId);

        if (userTenantToRemove.getId().equals(currentUserTenant.getId())) {
            throw new BadRequestException("Você não pode remover a si mesmo da obra");
        }

        UserObraPermitidaKey key = UserObraPermitidaKey.from(userTenantToRemove, obraId);

        UserObraPermitidaEntity userObraPermitida = userObraPermitidaRepository.findById(key)
                .orElseThrow(() -> new BadRequestException("Usuário não está associado a esta obra"));

        userTenantToRemove.removeObraPermitida(userObraPermitida);

        userObraPermitidaRepository.delete(userObraPermitida);
    }
}


