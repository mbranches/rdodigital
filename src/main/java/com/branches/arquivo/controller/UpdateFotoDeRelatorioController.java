package com.branches.arquivo.controller;

import com.branches.arquivo.dto.UpdateArquivoRequest;
import com.branches.arquivo.service.UpdateFotoDeRelatorioService;
import com.branches.config.security.UserTenantsContext;
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
@Tag(name = "Arquivo")
public class UpdateFotoDeRelatorioController {
    private final UpdateFotoDeRelatorioService updateFotoDeRelatorioService;

    @PutMapping("/api/tenants/{tenantExternalId}/relatorios/{relatorioExternalId}/fotos/{arquivoId}")
    @Operation(summary = "Update foto de relatorio", description = "Atualiza uma foto de relatório")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Foto atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Foto não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Void> execute(@RequestBody @Valid UpdateArquivoRequest request, @PathVariable String tenantExternalId,
                                        @PathVariable String relatorioExternalId, @PathVariable Long arquivoId) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        updateFotoDeRelatorioService.execute(request, arquivoId, relatorioExternalId, tenantExternalId, userTenants);

        return ResponseEntity.noContent().build();
    }

}
