package com.branches.relatorio.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.relatorio.dto.request.CreateRelatorioRequest;
import com.branches.relatorio.dto.response.CreateRelatorioResponse;
import com.branches.relatorio.service.CreateRelatorioService;
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
@Tag(name = "Relatorio")
public class CreateRelatorioController {
    private final CreateRelatorioService createRelatorioService;

    @PostMapping("/api/tenants/{tenantExternalId}/relatorios")
    @Operation(summary = "Create relatorio", description = "Cria um novo relatório")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Relatório criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<CreateRelatorioResponse> execute(@PathVariable String tenantExternalId, @RequestBody @Valid CreateRelatorioRequest request) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        CreateRelatorioResponse response = createRelatorioService.execute(request, tenantExternalId, userTenants);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
