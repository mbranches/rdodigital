package com.branches.obra.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.obra.dto.response.GetObraDetailsByIdExternoResponse;
import com.branches.obra.service.GetObraDetailsByIdExternoService;
import com.branches.user.domain.UserTenantEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class GetObraDetailsByIdExternoController {
    private final GetObraDetailsByIdExternoService getObraDetailsByIdExternoService;

    @GetMapping("/api/tenants/{tenantExternalId}/obras/{obraExternalId}")
    public ResponseEntity<GetObraDetailsByIdExternoResponse> execute(@PathVariable String tenantExternalId,
                                                                     @PathVariable String obraExternalId) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        GetObraDetailsByIdExternoResponse response = getObraDetailsByIdExternoService.execute(obraExternalId, tenantExternalId, userTenants);

        return ResponseEntity.ok(response);

    }
}
