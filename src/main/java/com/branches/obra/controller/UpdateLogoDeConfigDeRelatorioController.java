package com.branches.obra.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.obra.controller.enums.TipoLogoDeConfiguracaoDeRelatorio;
import com.branches.relatorio.dto.request.UpdateLogoDeConfigDeRelatorioRequest;
import com.branches.obra.service.UpdateLogoDeConfigDeRelatorioService;
import com.branches.usertenant.domain.UserTenantEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class UpdateLogoDeConfigDeRelatorioController {
    private final UpdateLogoDeConfigDeRelatorioService updateLogoDeConfigDeRelatorioService;

    @PutMapping("/api/tenants/{tenantExternalId}/obras/{obraExternalId}/configuracao-relatorios/logos")
    public ResponseEntity<Void> execute(@RequestBody @Valid UpdateLogoDeConfigDeRelatorioRequest request,
                                        @RequestParam TipoLogoDeConfiguracaoDeRelatorio tipoLogo,
                                        @PathVariable String obraExternalId,
                                        @PathVariable String tenantExternalId) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        updateLogoDeConfigDeRelatorioService.execute(request, tipoLogo, obraExternalId, tenantExternalId, userTenants);

        return ResponseEntity.noContent().build();
    }
}
