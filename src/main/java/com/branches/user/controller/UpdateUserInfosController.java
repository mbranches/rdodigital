package com.branches.user.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.user.dto.request.UpdateUserInfosRequest;
import com.branches.user.service.UpdateUserInfosService;
import com.branches.usertenant.domain.UserTenantEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Tag(name = "User")
public class UpdateUserInfosController {
    private final UpdateUserInfosService updateUserInfosService;

    @PutMapping("/api/tenants/{tenantExternalId}/users/me")
    @Operation(summary = "Update user infos", description = "Atualiza as informações do usuário logado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Informações do usuário atualizadas com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Void> execute(@RequestBody @Valid UpdateUserInfosRequest request, @PathVariable String tenantExternalId) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        updateUserInfosService.execute(tenantExternalId, userTenants, request);

        return ResponseEntity.noContent().build();
    }
}
