package com.branches.arquivo.controller;

import com.branches.arquivo.dto.request.CreateVideoDeRelatorioRequest;
import com.branches.arquivo.dto.response.CreateVideoDeRelatorioResponse;
import com.branches.arquivo.service.CreateVideoDeRelatorioService;
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
public class CreateVideoDeRelatorioController {
    private final CreateVideoDeRelatorioService createVideoDeRelatorioService;

    @PostMapping("/api/tenants/{tenantExternalId}/relatorios/{relatorioExternalId}/videos")
    @Operation(summary = "Create video de relatorio", description = "Cria um novo vídeo de relatório")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Vídeo criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Relatório não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<CreateVideoDeRelatorioResponse> execute(
            @RequestBody @Valid CreateVideoDeRelatorioRequest request,
            @PathVariable String tenantExternalId,
            @PathVariable String relatorioExternalId
    ) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        CreateVideoDeRelatorioResponse response = createVideoDeRelatorioService.execute(
                request,
                tenantExternalId,
                relatorioExternalId,
                userTenants
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

