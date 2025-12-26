package com.branches.material.service;

import com.branches.exception.ForbiddenException;
import com.branches.usertenant.domain.UserTenantEntity;
import org.springframework.stereotype.Service;

@Service
public class CheckIfUserHasAccessToMaterialService {
    public void execute(UserTenantEntity currentUserTenant) {
        if (currentUserTenant.getAuthorities().getCadastros().getMateriais()) return;

        throw new ForbiddenException();
    }
}
