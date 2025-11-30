package com.branches.obra.service;

import com.branches.exception.ForbiddenException;
import com.branches.usertenant.domain.UserTenantEntity;
import org.springframework.stereotype.Service;

@Service
public class CheckIfUserCanEditObraService {
    public void execute(UserTenantEntity userTenant, Long obraId) {
        if (!userTenant.getAuthorities().getObras().getCanCreateAndEdit() || !(userTenant.isAdministrador() || userTenant.getObrasPermitidasIds().contains(obraId))) {
            throw new ForbiddenException();
        }
    }
}
