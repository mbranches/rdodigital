package com.branches.relatorio.service;

import com.branches.exception.ForbiddenException;
import com.branches.usertenant.domain.UserTenantEntity;
import org.springframework.stereotype.Service;

@Service
public class CheckIfUserHasAccessToEditRelatorioService {
    public void execute(UserTenantEntity currentUserTenant) {
        if (currentUserTenant.getAuthorities().getRelatorios().getCanCreateAndEdit()) return;

        throw new ForbiddenException();
    }
}
