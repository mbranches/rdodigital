package com.branches.material.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.material.dto.request.UpdateMaterialRequest;
import com.branches.material.service.UpdateMaterialService;
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
@Tag(name = "Material")
public class UpdateMaterialController {
    private final UpdateMaterialService updateMaterialService;

    @PutMapping("/api/tenants/{tenantExternalId}/materiais/{id}")
    @Operation(summary = "Update material", description = "Atualiza um material")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Material atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Material não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Void> execute(@PathVariable String tenantExternalId, @PathVariable Long id, @RequestBody @Valid UpdateMaterialRequest request) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        updateMaterialService.execute(id, request, tenantExternalId, userTenants);

        return ResponseEntity.noContent().build();
    }
}
