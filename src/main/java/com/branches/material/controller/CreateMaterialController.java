package com.branches.material.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.material.dto.request.CreateMaterialRequest;
import com.branches.material.dto.response.MaterialResponse;
import com.branches.material.service.CreateMaterialService;
import com.branches.usertenant.domain.UserTenantEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Tag(name = "Material")
public class CreateMaterialController {
    private final CreateMaterialService createMaterialService;

    @PostMapping("/api/tenants/{tenantExternalId}/materiais")
    @Operation(summary = "Create material", description = "Cria um novo material")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Material criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<MaterialResponse> execute(@PathVariable String tenantExternalId, @RequestBody @Valid CreateMaterialRequest request) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        MaterialResponse response = createMaterialService.execute(tenantExternalId, request, userTenants);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
