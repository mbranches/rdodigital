package com.branches.maodeobra.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.obra.domain.enums.TipoMaoDeObra;
import com.branches.maodeobra.dto.response.MaoDeObraResponse;
import com.branches.maodeobra.service.ListAllMaoDeObraService;
import com.branches.usertenant.domain.UserTenantEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ListAllMaoDeObraController {
    private final ListAllMaoDeObraService listAllMaoDeObraService;

    @GetMapping("api/tenants/{tenantExternalId}/mao-de-obra")
    public ResponseEntity<List<MaoDeObraResponse>> execute(@PathVariable String tenantExternalId, @RequestParam TipoMaoDeObra tipoMaoDeObra) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        List<MaoDeObraResponse> response = listAllMaoDeObraService.execute(tenantExternalId, tipoMaoDeObra, userTenants);

        return ResponseEntity.ok(response);
    }
}
