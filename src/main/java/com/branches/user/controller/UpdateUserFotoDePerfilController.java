package com.branches.user.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.user.domain.UserEntity;
import com.branches.user.dto.request.UpdateUserFotoDePerfilRequest;
import com.branches.user.service.UpdateUserFotoDePerfilService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class UpdateUserFotoDePerfilController {
    private final UpdateUserFotoDePerfilService updateUserFotoDePerfilService;

    @PatchMapping("/api/users/me/foto-de-perfil")
    public ResponseEntity<Void> execute(@RequestBody @Valid UpdateUserFotoDePerfilRequest request) {
        UserEntity user = UserTenantsContext.getUser();

        updateUserFotoDePerfilService.execute(user, request);

        return ResponseEntity.noContent().build();
    }
}
