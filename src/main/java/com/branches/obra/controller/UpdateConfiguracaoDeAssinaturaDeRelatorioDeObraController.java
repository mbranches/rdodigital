package com.branches.obra.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.obra.dto.request.UpdateConfigDeAssinaturaDeRelatorioDeObraRequest;
import com.branches.obra.service.UpdateConfiguracaoDeAssinaturaDeRelatorioDeObraService;
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
@Tag(name = "Obra")
public class UpdateConfiguracaoDeAssinaturaDeRelatorioDeObraController {
    private final UpdateConfiguracaoDeAssinaturaDeRelatorioDeObraService updateConfiguracaoDeAssinaturaDeRelatorioDeObraService;

    @PutMapping("/api/tenants/{tenantExternalId}/obras/{obraExternalId}/configuracao-relatorios/assinaturas/{id}")
    @Operation(summary = "Update configuracao de assinatura de relatorio de obra", description = "Atualiza uma configuração de assinatura de relatório de obra")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Configuração de assinatura atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Configuração não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Void> execute(@RequestBody @Valid UpdateConfigDeAssinaturaDeRelatorioDeObraRequest request, @PathVariable String obraExternalId, @PathVariable String tenantExternalId, @PathVariable Long id) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        updateConfiguracaoDeAssinaturaDeRelatorioDeObraService.execute(request, id, obraExternalId, tenantExternalId, userTenants);

        return ResponseEntity.noContent().build();
    }
}
