package com.branches.obra.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.obra.dto.response.ConfiguracaoRelatoriosResponse;
import com.branches.obra.service.GetConfiguracaoRelatoriosService;
import com.branches.usertenant.domain.UserTenantEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class GetConfiguracoesDeRelatorioDeObraController {
    private final GetConfiguracaoRelatoriosService getConfiguracaoRelatoriosService;

    @GetMapping("/api/tenants/{tenantExternalId}/obras/{obraExternalId}/configuracao-relatorios")
    public ResponseEntity<ConfiguracaoRelatoriosResponse> execute(@PathVariable String tenantExternalId, @PathVariable String obraExternalId) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        ConfiguracaoRelatoriosResponse response = getConfiguracaoRelatoriosService.execute(tenantExternalId, obraExternalId, userTenants);

        return ResponseEntity.ok(response);
    }

}
