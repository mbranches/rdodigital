package com.branches.usertenant.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.usertenant.dto.response.UserTenantInfoResponse;
import com.branches.usertenant.service.GetUserTenantInfoService;
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
@Tag(name = "UserTenant")
public class GetUserTenantInfoController {
    private final GetUserTenantInfoService getUserTenantInfoService;

    @GetMapping("/api/tenants/{tenantExternalId}/users/me/info")
    @Operation(summary = "Get user tenant info", description = "Obtém as informações do usuário logado em um tenant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Informações do usuário recuperadas com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Tenant não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<UserTenantInfoResponse> execute(@PathVariable String tenantExternalId) {
        Long userId = UserTenantsContext.getUserId();
        List<Long> tenantIds = UserTenantsContext.getTenantIds();

        UserTenantInfoResponse response = getUserTenantInfoService.     execute(tenantExternalId, userId, tenantIds);

        return ResponseEntity.ok(response);
    }
}
