package com.branches.atividade.service;

import com.branches.exception.ForbiddenException;
import com.branches.usertenant.domain.UserTenantEntity;
import org.springframework.stereotype.Service;

@Service
public class CheckIfUserCanViewAtividadesService {
    public void execute(UserTenantEntity userTenant) {
        if (userTenant.getAuthorities().getItensDeRelatorio().getAtividades()) return;

        throw new ForbiddenException();
    }
}
