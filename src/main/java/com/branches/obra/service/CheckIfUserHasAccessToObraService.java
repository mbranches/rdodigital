package com.branches.obra.service;

import com.branches.exception.ForbiddenException;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.domain.enums.PerfilUserTenant;
import org.springframework.stereotype.Service;

@Service
public class CheckIfUserHasAccessToObraService {
    public void execute(UserTenantEntity userTenant, Long obraId) {
        boolean userHasAccessToObra = userTenant.getPerfil().equals(PerfilUserTenant.ADMINISTRADOR)
                || userTenant.getObrasPermitidasIds().contains(obraId);

        if (!userHasAccessToObra) throw new ForbiddenException();
    }
}
