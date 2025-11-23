package com.branches.maodeobra.service;

import com.branches.exception.ForbiddenException;
import com.branches.usertenant.domain.UserTenantEntity;
import org.springframework.stereotype.Service;

@Service
public class CheckIfUserCanViewMaoDeObraService {
    public void execute(UserTenantEntity userTenant) {
        if (userTenant.getAuthorities().getItensDeRelatorio().getMaoDeObra()) return;

        throw new ForbiddenException();
    }
}
