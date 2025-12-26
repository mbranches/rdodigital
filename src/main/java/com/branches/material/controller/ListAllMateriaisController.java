package com.branches.material.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.material.dto.response.MaterialResponse;
import com.branches.material.service.ListAllMateriaisService;
import com.branches.usertenant.domain.UserTenantEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Tag(name = "Material")
public class ListAllMateriaisController {
    private final ListAllMateriaisService listAllMateriaisService;

    @GetMapping("/api/tenants/{externalTenantId}/materiais")
    @Operation(summary = "List all materiais", description = "Lista todos os materiais")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de materiais recuperada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<List<MaterialResponse>> execute(@PathVariable String externalTenantId) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        List<MaterialResponse> response = listAllMateriaisService.execute(externalTenantId, userTenants);

        return ResponseEntity.ok(response);
    }
}
