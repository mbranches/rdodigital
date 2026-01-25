package com.branches.material.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.material.dto.response.GetItemTopMateriaisResponse;
import com.branches.material.service.GetTopMateriaisService;
import com.branches.shared.pagination.PageResponse;
import com.branches.shared.pagination.PageableRequest;
import com.branches.usertenant.domain.UserTenantEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Material")
@RequiredArgsConstructor
@RestController
public class GetTopMateriaisController {
    private final GetTopMateriaisService getTopMateriaisService;

    @Operation(summary = "Get top materiais", description = "Lista os materiais mais utilizados em relatórios")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de materiais recuperada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Relatório não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/api/tenants/{tenantExternalId}/materiais/analisys/top")
    public ResponseEntity<PageResponse<GetItemTopMateriaisResponse>> execute(
            @Valid PageableRequest pageableRequest,
            @PathVariable String tenantExternalId,
            @RequestParam(required = false) String obraExternalId
    ) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        PageResponse<GetItemTopMateriaisResponse> response = getTopMateriaisService.execute(
                tenantExternalId,
                obraExternalId,
                userTenants,
                pageableRequest
        );

        return ResponseEntity.ok(response);
    }
}
