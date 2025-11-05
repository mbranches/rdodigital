package com.branches.shared.dto;

import com.branches.user.domain.UserEntity;
import com.branches.user.domain.UserTenantEntity;
import com.branches.user.domain.enums.PerfilUserTenant;
import com.branches.user.domain.enums.Role;

import java.util.List;

public record UserDto(
        Long id,
        String idExterno,
        String nome,
        String email,
        String password,
        String cargo,
        Role role,
        String fotoUrl,
        Boolean ativo,
        List<Long> tenantsIds,
        List<UserTenantDto> tenants,
        List<Long> obrasPermitidasIds
) {
    public static UserDto of(UserEntity user) {
        return new UserDto(
                user.getId(),
                user.getIdExterno(),
                user.getNome(),
                user.getEmail(),
                user.getPassword(),
                user.getCargo(),
                user.getRole(),
                user.getFotoUrl(),
                user.getAtivo(),
                user.getTenantsIds(),
                user.getUserTenantEntities().stream().map(UserTenantDto::from).toList(),
                user.getObrasPermitidasIds()
        );
    }

    public record UserTenantDto(
            Long tenantId,
            PerfilUserTenant perfil
    ) {
        public static UserTenantDto from(UserTenantEntity entity) {
            return new UserTenantDto(
                    entity.getTenantId(),
                    entity.getPerfil()
            );
        }
    }
}