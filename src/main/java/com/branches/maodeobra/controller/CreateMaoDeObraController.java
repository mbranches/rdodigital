package com.branches.maodeobra.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.maodeobra.dto.request.CreateMaoDeObraRequest;
import com.branches.maodeobra.dto.response.CreateMaoDeObraResponse;
import com.branches.maodeobra.service.CreateMaoDeObraService;
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
@Tag(name = "MaoDeObra")
public class CreateMaoDeObraController {
    private final CreateMaoDeObraService createMaoDeObraService;

    @PostMapping("/api/tenants/{tenantExternalId}/mao-de-obra")
    @Operation(summary = "Create mao de obra", description = "Cria uma nova mão de obra")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Mão de obra criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<CreateMaoDeObraResponse> execute(@RequestBody @Valid CreateMaoDeObraRequest request, @PathVariable String tenantExternalId) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        CreateMaoDeObraResponse response = createMaoDeObraService.execute(request, tenantExternalId, userTenants);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
