package com.branches.usertenant.service;

import com.branches.exception.ForbiddenException;
import com.branches.usertenant.domain.UserTenantEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetCurrentUserTenantService {
    public UserTenantEntity execute(List<UserTenantEntity> userTenants, Long tenantToUse) {
        return userTenants.stream()
                .filter(ut -> ut.getTenantId().equals(tenantToUse))
                .findFirst()
                .orElseThrow(ForbiddenException::new);
    }
}
