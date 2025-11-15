package com.branches.user.dto.response;

import com.branches.user.domain.UserEntity;

public record CreateUserResponse(
        String id,
        String nome,
        String email,
        String cargo
) {
    public static CreateUserResponse from(UserEntity user) {
        return new CreateUserResponse(
                user.getIdExterno(),
                user.getNome(),
                user.getEmail(),
                user.getCargo()
        );
    }
}
