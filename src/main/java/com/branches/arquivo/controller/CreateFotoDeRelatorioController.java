package com.branches.arquivo.controller;

import com.branches.arquivo.dto.request.CreateFotoDeRelatorioRequest;
import com.branches.arquivo.dto.response.FotoDeRelatorioResponse;
import com.branches.arquivo.service.CreateFotoDeRelatorioService;
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
@Tag(name = "Arquivo")
public class CreateFotoDeRelatorioController {
    private final CreateFotoDeRelatorioService createFotoDeRelatorioService;

    @PostMapping("/api/tenants/{tenantExternalId}/relatorios/{relatorioExternalId}/fotos")
    @Operation(summary = "Create foto de relatorio", description = "Cria uma nova foto de relatório")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Foto criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Relatório não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<FotoDeRelatorioResponse> execute(
            @RequestBody @Valid CreateFotoDeRelatorioRequest request,
            @PathVariable String tenantExternalId,
            @PathVariable String relatorioExternalId
    ) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        FotoDeRelatorioResponse response = createFotoDeRelatorioService.execute(
                request,
                tenantExternalId,
                relatorioExternalId,
                userTenants
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
