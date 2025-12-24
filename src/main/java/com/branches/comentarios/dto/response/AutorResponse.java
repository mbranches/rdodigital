package com.branches.comentarios.dto.response;

import com.branches.user.domain.UserEntity;

public record AutorResponse(
        Long id,
        String nome,
        String email,
        String fotoPerfilUrl
) {
    public static AutorResponse from(UserEntity autorEntity) {
        return new AutorResponse(
                autorEntity.getId(),
                autorEntity.getNome(),
                autorEntity.getEmail(),
                autorEntity.getFotoUrl()
        );
    }
}
