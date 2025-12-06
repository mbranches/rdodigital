package com.branches.arquivo.controller;

import com.branches.arquivo.domain.enums.TipoArquivo;
import com.branches.arquivo.dto.response.ArquivoResponse;
import com.branches.arquivo.service.ListArquivosDeRelatorioService;
import com.branches.config.security.UserTenantsContext;
import com.branches.usertenant.domain.UserTenantEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ListArquivosDeRelatorioController {

    private final ListArquivosDeRelatorioService listArquivosDeRelatorioService;

    @GetMapping("/api/tenants/{tenantExternalId}/relatorios/{relatorioExternalId}/arquivos")
    public ResponseEntity<List<ArquivoResponse>> execute(@PathVariable String tenantExternalId,
                                                         @PathVariable String relatorioExternalId,
                                                         @RequestParam TipoArquivo tipo) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        List<ArquivoResponse> response = listArquivosDeRelatorioService.execute(relatorioExternalId, tenantExternalId, tipo, userTenants);

        return ResponseEntity.ok(response);
    }

}
