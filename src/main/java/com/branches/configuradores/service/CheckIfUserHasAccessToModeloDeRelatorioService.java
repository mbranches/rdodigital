package com.branches.configuradores.service;

import com.branches.exception.ForbiddenException;
import com.branches.usertenant.domain.UserTenantEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CheckIfUserHasAccessToModeloDeRelatorioService {
    public void execute(UserTenantEntity userTenant) {
        if (userTenant.getAuthorities().getCadastros().getModelosDeRelatorio()) return;

        throw new ForbiddenException();
    }
}
