package com.branches.equipamento.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.equipamento.service.UpdateEquipamentoDeRelatorioService;
import com.branches.equipamento.dto.request.UpdateEquipamentoDeRelatorioRequest;
import com.branches.usertenant.domain.UserTenantEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Tag(name = "Equipamento")
public class UpdateEquipamentoDeRelatorioController {
    private final UpdateEquipamentoDeRelatorioService updateEquipamentoDeRelatorioService;

    @PutMapping("/api/tenants/{tenantExternalId}/relatorios/{relatorioExternalId}/equipamentos/{id}")
    @Operation(summary = "Update equipamento de relatorio", description = "Atualiza um equipamento de relatório")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Equipamento de relatório atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Equipamento não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Void> execute(@RequestBody @Valid UpdateEquipamentoDeRelatorioRequest request, @PathVariable String tenantExternalId, @PathVariable String relatorioExternalId, @PathVariable Long id) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        updateEquipamentoDeRelatorioService.execute(request, id, relatorioExternalId, tenantExternalId, userTenants);

        return ResponseEntity.noContent().build();
    }
}
