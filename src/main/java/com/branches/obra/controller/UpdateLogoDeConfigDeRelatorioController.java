package com.branches.obra.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.obra.controller.enums.TipoLogoDeConfiguracaoDeRelatorio;
import com.branches.relatorio.dto.request.UpdateLogoDeConfigDeRelatorioRequest;
import com.branches.obra.service.UpdateLogoDeConfigDeRelatorioService;
import com.branches.usertenant.domain.UserTenantEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Tag(name = "Obra")
public class UpdateLogoDeConfigDeRelatorioController {
    private final UpdateLogoDeConfigDeRelatorioService updateLogoDeConfigDeRelatorioService;

    @PutMapping("/api/tenants/{tenantExternalId}/obras/{obraExternalId}/configuracao-relatorios/logos")
    @Operation(summary = "Update logo de config de relatorio", description = "Atualiza o logo da configuração de relatório")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Logo atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Obra não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Void> execute(@RequestBody @Valid UpdateLogoDeConfigDeRelatorioRequest request,
                                        @RequestParam TipoLogoDeConfiguracaoDeRelatorio tipoLogo,
                                        @PathVariable String obraExternalId,
                                        @PathVariable String tenantExternalId) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        updateLogoDeConfigDeRelatorioService.execute(request, tipoLogo, obraExternalId, tenantExternalId, userTenants);

        return ResponseEntity.noContent().build();
    }
}
