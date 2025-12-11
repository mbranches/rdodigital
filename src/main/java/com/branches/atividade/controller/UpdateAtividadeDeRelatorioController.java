package com.branches.atividade.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.atividade.service.UpdateAtividadeDeRelatorioService;
import com.branches.atividade.dto.request.UpdateAtividadeDeRelatorioRequest;
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
@Tag(name = "Atividade")
public class UpdateAtividadeDeRelatorioController {
    private final UpdateAtividadeDeRelatorioService updateAtividadeDeRelatorioService;

    @PutMapping("/api/tenants/{tenantExternalId}/relatorios/{relatorioExternalId}/atividades/{id}")
    @Operation(summary = "Update atividade de relatorio", description = "Atualiza uma atividade de relatório")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Atividade atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Atividade não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Void> execute(@RequestBody @Valid UpdateAtividadeDeRelatorioRequest request, @PathVariable String tenantExternalId, @PathVariable String relatorioExternalId, @PathVariable Long id) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        updateAtividadeDeRelatorioService.execute(request, id, relatorioExternalId, tenantExternalId, userTenants);

        return ResponseEntity.noContent().build();
    }
}
