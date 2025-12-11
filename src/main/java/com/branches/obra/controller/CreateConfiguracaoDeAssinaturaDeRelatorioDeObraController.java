package com.branches.obra.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.obra.dto.request.CreateConfigDeAssinaturaDeRelatorioDeObraRequest;
import com.branches.obra.dto.response.ConfiguracaoDeAssinaturaDeRelatorioResponse;
import com.branches.obra.service.CreateConfiguracaoDeAssinaturaDeRelatorioDeObraService;
import com.branches.usertenant.domain.UserTenantEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Tag(name = "Obra")
public class CreateConfiguracaoDeAssinaturaDeRelatorioDeObraController {
    private final CreateConfiguracaoDeAssinaturaDeRelatorioDeObraService createConfiguracaoDeAssinaturaDeRelatorioDeObraService;

    @PostMapping("/api/tenants/{tenantExternalId}/obras/{obraExternalId}/configuracao-relatorios/assinaturas")
    @Operation(summary = "Create configuracao de assinatura de relatorio de obra", description = "Cria uma configuração de assinatura de relatório de obra")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Configuração de assinatura criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Obra não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<ConfiguracaoDeAssinaturaDeRelatorioResponse> execute(@RequestBody @Valid CreateConfigDeAssinaturaDeRelatorioDeObraRequest request, @PathVariable String obraExternalId, @PathVariable String tenantExternalId) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        var response = createConfiguracaoDeAssinaturaDeRelatorioDeObraService.execute(request, obraExternalId, tenantExternalId, userTenants);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
