package com.branches.usertenant.service;

import com.branches.exception.ForbiddenException;
import com.branches.exception.NotFoundException;
import com.branches.tenant.domain.TenantEntity;
import com.branches.tenant.repository.TenantRepository;
import com.branches.tenant.repository.projection.TenantInfoProjection;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.user.repository.UserRepository;
import com.branches.user.repository.projection.UserInfoProjection;
import com.branches.usertenant.dto.response.UserTenantInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class GetUserTenantInfoService {

    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;

    public UserTenantInfoResponse execute(String tenantExternalId, Long userId, List<Long> tenantIds) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        checkIfUserBelongsToTenant(tenantId, tenantIds);

        UserInfoProjection user = userRepository.findUserInfoByIdAndTenantId(userId, tenantId)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado para o tenant informado"));
        List<TenantEntity> allTenantsByUser = tenantRepository.findAllByIdInAndAtivoIsTrue(tenantIds);

        TenantInfoProjection tenant = tenantRepository.findTenantInfoById(tenantId)
                .orElseThrow(() -> new NotFoundException("Tenant não encontrado"));

        return UserTenantInfoResponse.from(user, allTenantsByUser, tenant);
    }

    private void checkIfUserBelongsToTenant(Long tenantId, List<Long> tenantIds) {
        if (!tenantIds.contains(tenantId)) {
            throw new ForbiddenException();
        }
    }
}
