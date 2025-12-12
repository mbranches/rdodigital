package com.branches.obra.service;

import com.branches.obra.dto.request.AdicionaUserToObraRequest;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserObraPermitidaEntity;
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
public class AddUserToObraService {
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetObraIdByIdExternoService getObraIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final CheckIfUserCanEditObraService checkIfUserCanEditObraService;
    private final GetUserTenantByUserIdExternoAndTenantIdService getUserTenantByUserIdExternoAndTenantIdService;
    private final UserObraPermitidaRepository userObraPermitidaRepository;

    public void execute(AdicionaUserToObraRequest request, String obraExternalId, String tenantExternalId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        Long obraId = getObraIdByIdExternoService.execute(obraExternalId, tenantId);

        checkIfUserCanEditObraService.execute(currentUserTenant, obraId);

        UserTenantEntity userTenantToAddObra = getUserTenantByUserIdExternoAndTenantIdService.execute(request.userId(), tenantId);

        UserObraPermitidaEntity userObraPermitidaEntity = UserObraPermitidaEntity.builder()
                .obraId(obraId)
                .userTenant(userTenantToAddObra)
                .build();
        userObraPermitidaEntity.setarId();

        userObraPermitidaRepository.save(userObraPermitidaEntity);
    }
}
