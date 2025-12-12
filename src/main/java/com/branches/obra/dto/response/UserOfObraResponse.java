package com.branches.obra.dto.response;

import com.branches.user.domain.UserEntity;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.domain.enums.PerfilUserTenant;

public record UserOfObraResponse(
        String id,
        String nome,
        String email,
        String cargo,
        PerfilUserTenant perfil,
        String fotoUrl
) {
    public static UserOfObraResponse from(UserTenantEntity userTenant) {
        UserEntity user = userTenant.getUser();
        return new UserOfObraResponse(
                user.getIdExterno(),
                user.getNome(),
                user.getEmail(),
                userTenant.getCargo(),
                userTenant.getPerfil(),
                user.getFotoUrl()
        );
    }
}
