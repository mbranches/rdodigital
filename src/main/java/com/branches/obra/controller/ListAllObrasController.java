package com.branches.obra.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.obra.dto.response.ObraByListAllResponse;
import com.branches.obra.service.ListAllObrasService;
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
@Tag(name = "Obra")
public class ListAllObrasController {
    private final ListAllObrasService listAllObrasService;

    @GetMapping("/api/tenants/{tenantExternalId}/obras")
    @Operation(summary = "List all obras", description = "Lista todas as obras")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de obras recuperada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<List<ObraByListAllResponse>> execute(@PathVariable String tenantExternalId) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        List<ObraByListAllResponse> response = listAllObrasService.execute(tenantExternalId, userTenants);

        return ResponseEntity.ok(response);
    }
}
