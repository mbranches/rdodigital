package com.branches.obra.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.obra.dto.request.UpdateConfiguracoesDeRelatoriosDeObraRequest;
import com.branches.obra.service.UpdateConfiguracoesDeRelatoriosDeObraService;
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
public class UpdateConfiguracoesDeRelatoriosDeObraController {
    private final UpdateConfiguracoesDeRelatoriosDeObraService updateConfiguracoesDeRelatoriosDeObraService;

    @PutMapping("/api/tenants/{tenantExternalId}/obras/{obraExternalId}/configuracao-relatorios")
    @Operation(summary = "Update configuracoes de relatorios de obra", description = "Atualiza as configurações de relatórios de uma obra")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Configurações atualizadas com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Obra não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Void> execute(@RequestBody @Valid UpdateConfiguracoesDeRelatoriosDeObraRequest request, @PathVariable String obraExternalId, @PathVariable String tenantExternalId) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        updateConfiguracoesDeRelatoriosDeObraService.execute(request, obraExternalId, tenantExternalId, userTenants);

        return ResponseEntity.noContent().build();
    }
}
