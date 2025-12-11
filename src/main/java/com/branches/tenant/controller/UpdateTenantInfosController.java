package com.branches.tenant.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.tenant.dto.request.UpdateTenantInfosRequest;
import com.branches.tenant.service.UpdateTenantInfosService;
import com.branches.usertenant.domain.UserTenantEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Tag(name = "Tenant")
public class UpdateTenantInfosController {
    private final UpdateTenantInfosService updateTenantInfosService;

    @PutMapping("/api/tenants/{tenantExternalId}")
    @Operation(summary = "Update tenant infos", description = "Atualiza as informações de um tenant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Informações do tenant atualizadas com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Tenant não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Void> execute(@PathVariable String tenantExternalId, @RequestBody @Valid UpdateTenantInfosRequest request) {
        List<UserTenantEntity> userTenantEntities = UserTenantsContext.getUserTenants();

        updateTenantInfosService.execute(tenantExternalId, userTenantEntities, request);

        return ResponseEntity.noContent().build();

    }
}
