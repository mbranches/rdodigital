package com.branches.shared.dto;

import com.branches.user.domain.UserEntity;
import com.branches.user.domain.UserTenantId;
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
        List<Long> tenantIds
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
                user.getUserTenantIds().stream().map(UserTenantId::getTenantId).toList()
        );
    }
}