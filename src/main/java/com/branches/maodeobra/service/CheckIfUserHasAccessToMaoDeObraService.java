package com.branches.maodeobra.service;

import com.branches.exception.ForbiddenException;
import com.branches.usertenant.domain.UserTenantEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CheckIfUserHasAccessToMaoDeObraService {
    public void execute(UserTenantEntity userTenant) {
        if (userTenant.getAuthorities().getCadastros().getMaoDeObra()) return;

        throw new ForbiddenException();
    }
}
