package com.branches.relatorio.service;

import com.branches.exception.ForbiddenException;
import com.branches.relatorio.domain.enums.StatusRelatorio;
import com.branches.user.domain.PermissionsRelatorio;
import com.branches.usertenant.domain.UserTenantEntity;
import org.springframework.stereotype.Service;

@Service
public class CheckIfUserHasAccessToEditRelatorioService {
    public void execute(UserTenantEntity currentUserTenant, StatusRelatorio statusRelatorio) {
        PermissionsRelatorio permissionsRelatorio = currentUserTenant.getAuthorities().getRelatorios();

        if (permissionsRelatorio.getCanCreateAndEdit() && (!permissionsRelatorio.getCanViewOnlyAprovados() || !statusRelatorio.equals(StatusRelatorio.APROVADO))) return;

        throw new ForbiddenException();
    }
}
