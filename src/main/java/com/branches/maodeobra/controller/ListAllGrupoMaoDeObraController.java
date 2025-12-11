package com.branches.maodeobra.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.maodeobra.dto.response.GrupoMaoDeObraResponse;
import com.branches.maodeobra.service.ListAllGruposMaoDeObraService;
import com.branches.usertenant.domain.UserTenantEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Tag(name = "MaoDeObra")
public class ListAllGrupoMaoDeObraController {
    private final ListAllGruposMaoDeObraService listAllGruposMaoDeObraService;

    @GetMapping("/api/tenants/{tenantExternalId}/grupos-de-mao-de-obra")
    @Operation(summary = "List all grupo mao de obra", description = "Lista todos os grupos de mão de obra")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de grupos de mão de obra recuperada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<List<GrupoMaoDeObraResponse>> execute(@PathVariable String tenantExternalId) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        List<GrupoMaoDeObraResponse> response = listAllGruposMaoDeObraService.execute(tenantExternalId, userTenants);

        return ResponseEntity.ok(response);
    }
}

