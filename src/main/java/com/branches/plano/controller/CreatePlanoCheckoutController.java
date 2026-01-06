package com.branches.plano.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.plano.dto.request.CreatePlanoCheckoutRequest;
import com.branches.plano.dto.response.PlanoCheckoutResponse;
import com.branches.plano.service.CreatePlanoCheckoutService;
import com.branches.usertenant.domain.UserTenantEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Plano")
@RequiredArgsConstructor
@RestController
public class CreatePlanoCheckoutController {
    private final CreatePlanoCheckoutService createPlanoCheckoutService;

    @Operation(summary = "Create plano checkout", description = "Gera um link para o checkout do plano")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Link gerado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PostMapping("/api/tenants/{tenantExternalId}/plano-checkout")
    public ResponseEntity<PlanoCheckoutResponse> execute(
            @RequestBody @Valid CreatePlanoCheckoutRequest request,
            @PathVariable String tenantExternalId) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        PlanoCheckoutResponse response = createPlanoCheckoutService.execute(request, tenantExternalId, userTenants);

        return ResponseEntity.ok(response);
    }
}
