package com.branches.atividade.controller;

import com.branches.atividade.dto.request.CreateAtividadeDeRelatorioRequest;
import com.branches.atividade.dto.response.CreateAtividadeDeRelatorioResponse;
import com.branches.atividade.service.CreateAtividadeDeRelatorioService;
import com.branches.config.security.UserTenantsContext;
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
@Tag(name = "Atividade")
public class CreateAtividadeDeRelatorioController {
    private final CreateAtividadeDeRelatorioService createAtividadeDeRelatorioService;

    @PostMapping("/api/tenants/{tenantExternalId}/relatorios/{relatorioExternalId}/atividades")
    @Operation(summary = "Create atividade de relatorio", description = "Cria uma nova atividade de relatório")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Atividade criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Relatório não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<CreateAtividadeDeRelatorioResponse> execute(
            @RequestBody @Valid CreateAtividadeDeRelatorioRequest request,
            @PathVariable String tenantExternalId,
            @PathVariable String relatorioExternalId
    ) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        CreateAtividadeDeRelatorioResponse response = createAtividadeDeRelatorioService.execute(request, relatorioExternalId, tenantExternalId, userTenants);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
