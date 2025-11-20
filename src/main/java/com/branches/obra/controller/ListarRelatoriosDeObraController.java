package com.branches.obra.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.obra.service.ListarRelatoriosDeObraService;
import com.branches.relatorio.rdo.dto.response.RelatorioResponse;
import com.branches.usertenant.domain.UserTenantEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ListarRelatoriosDeObraController {
    private final ListarRelatoriosDeObraService listarRelatoriosDeObraService;

    @GetMapping("/api/tenants/{tenantExternalId}/obras/{obraExternalId}/relatorios")
    public ResponseEntity<List<RelatorioResponse>> execute(@PathVariable String tenantExternalId,
                                                           @PathVariable String obraExternalId,
                                                           @RequestParam int pageSize,
                                                           @RequestParam int pageNumber,
                                                           @RequestParam Sort.Direction sortDirection
                                                           ) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(sortDirection, "enversCreatedDate"));

        List<RelatorioResponse> response = listarRelatoriosDeObraService.execute(obraExternalId, tenantExternalId, userTenants, pageRequest);

        return ResponseEntity.ok(response);
    }
}
