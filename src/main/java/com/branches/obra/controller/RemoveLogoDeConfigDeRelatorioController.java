package com.branches.obra.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.obra.controller.enums.TipoLogoDeConfiguracaoDeRelatorio;
import com.branches.obra.service.RemoveLogoDeConfigDeRelatorioService;
import com.branches.usertenant.domain.UserTenantEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class RemoveLogoDeConfigDeRelatorioController {
    private final RemoveLogoDeConfigDeRelatorioService removeLogoDeConfigDeRelatorioService;

    @DeleteMapping("/api/tenants/{tenantExternalId}/obras/{obraExternalId}/configuracao-relatorios/logos")
    public ResponseEntity<Void> execute(@RequestParam TipoLogoDeConfiguracaoDeRelatorio tipoLogo,
                                        @PathVariable String obraExternalId,
                                        @PathVariable String tenantExternalId) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        removeLogoDeConfigDeRelatorioService.execute(tipoLogo, obraExternalId, tenantExternalId, userTenants);

        return ResponseEntity.noContent().build();
    }
}
