package com.branches.obra.dto.response;

import com.branches.user.domain.UserEntity;

public record UserOfObraResponse(
        String id,
        String nome,
        String fotoUrl
) {
    public static UserOfObraResponse from(UserEntity user) {
        return new UserOfObraResponse(
                user.getIdExterno(),
                user.getNome(),
                user.getFotoUrl()
        );
    }
}
