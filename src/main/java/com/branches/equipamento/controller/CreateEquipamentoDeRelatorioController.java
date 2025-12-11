package com.branches.equipamento.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.equipamento.dto.request.CreateEquipamentoDeRelatorioRequest;
import com.branches.equipamento.dto.response.CreateEquipamentoDeRelatorioResponse;
import com.branches.equipamento.service.CreateEquipamentoDeRelatorioService;
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
@Tag(name = "Equipamento")
public class CreateEquipamentoDeRelatorioController {
    private final CreateEquipamentoDeRelatorioService createEquipamentoDeRelatorioService;

    @PostMapping("/api/tenants/{tenantExternalId}/relatorios/{relatorioExternalId}/equipamentos")
    @Operation(summary = "Create equipamento de relatorio", description = "Cria um novo equipamento de relatório")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Equipamento de relatório criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Relatório não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<CreateEquipamentoDeRelatorioResponse> execute(
            @RequestBody @Valid CreateEquipamentoDeRelatorioRequest request,
            @PathVariable String tenantExternalId,
            @PathVariable String relatorioExternalId
    ) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        CreateEquipamentoDeRelatorioResponse response = createEquipamentoDeRelatorioService.execute(request, relatorioExternalId, tenantExternalId, userTenants);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
