package com.branches.obra.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.obra.service.ListarRelatoriosDeObraService;
import com.branches.relatorio.dto.response.RelatorioResponse;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.utils.PageResponse;
import com.branches.utils.PageableRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ListarRelatoriosDeObraController {
    private final ListarRelatoriosDeObraService listarRelatoriosDeObraService;

    @GetMapping("/api/tenants/{tenantExternalId}/obras/{obraExternalId}/relatorios")
    public ResponseEntity<PageResponse<RelatorioResponse>> execute(@PathVariable String tenantExternalId,
                                                                   @PathVariable String obraExternalId,
                                                                   @Valid PageableRequest pageableRequest
                                                           ) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        PageResponse<RelatorioResponse> response = listarRelatoriosDeObraService.execute(obraExternalId, tenantExternalId, userTenants, pageableRequest);

        return ResponseEntity.ok(response);
    }
}
