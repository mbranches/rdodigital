package com.branches.obra.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.obra.dto.request.UpdateGrupoDeObraRequest;
import com.branches.obra.service.UpdateGrupoDeObraService;
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
@Tag(name = "Obra")
public class UpdateGrupoDeObraController {
    private final UpdateGrupoDeObraService updateGrupoDeObraService;

    @PutMapping("/api/tenants/{tenantExternalId}/grupos-de-obra/{id}")
    @Operation(summary = "Update grupo de obra", description = "Atualiza um grupo de obra")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Grupo de obra atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Grupo de obra não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Void> execute(@PathVariable String tenantExternalId, @PathVariable Long id, @RequestBody @Valid UpdateGrupoDeObraRequest request) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        updateGrupoDeObraService.execute(id, tenantExternalId, request, userTenants);

        return ResponseEntity.noContent().build();
    }
}
