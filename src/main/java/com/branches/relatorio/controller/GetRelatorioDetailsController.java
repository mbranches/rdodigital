package com.branches.relatorio.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.relatorio.dto.response.GetRelatorioDetailsResponse;
import com.branches.relatorio.service.GetRelatorioDetailsService;
import com.branches.usertenant.domain.UserTenantEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class GetRelatorioDetailsController {
    private final GetRelatorioDetailsService getRelatorioDetailsService;

    @GetMapping("/api/tenants/{tenantExternalId}/relatorios/{relatorioExternalId}")
    public ResponseEntity<GetRelatorioDetailsResponse> execute(@PathVariable String tenantExternalId,
                                                               @PathVariable String relatorioExternalId) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        GetRelatorioDetailsResponse response = getRelatorioDetailsService.execute(
                tenantExternalId,
                relatorioExternalId,
                userTenants
        );

        return ResponseEntity.ok(response);
    }
}
