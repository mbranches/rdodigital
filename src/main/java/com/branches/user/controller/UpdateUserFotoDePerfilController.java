package com.branches.user.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.user.domain.UserEntity;
import com.branches.user.dto.request.UpdateUserFotoDePerfilRequest;
import com.branches.user.service.UpdateUserFotoDePerfilService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Tag(name = "User")
public class UpdateUserFotoDePerfilController {
    private final UpdateUserFotoDePerfilService updateUserFotoDePerfilService;

    @PatchMapping("/api/users/me/foto-de-perfil")
    @Operation(summary = "Update user foto de perfil", description = "Atualiza a foto de perfil do usuário logado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Foto de perfil atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Void> execute(@RequestBody @Valid UpdateUserFotoDePerfilRequest request) {
        UserEntity user = UserTenantsContext.getUser();

        updateUserFotoDePerfilService.execute(user, request);

        return ResponseEntity.noContent().build();
    }
}
