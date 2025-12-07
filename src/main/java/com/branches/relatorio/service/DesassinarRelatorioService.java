package com.branches.relatorio.service;

import com.branches.exception.NotFoundException;
import com.branches.external.aws.S3DeleteFile;
import com.branches.relatorio.domain.AssinaturaDeRelatorioEntity;
import com.branches.relatorio.domain.RelatorioEntity;
import com.branches.relatorio.repository.AssinaturaDeRelatorioRepository;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class DesassinarRelatorioService {
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final CheckIfUserHasAccessToEditRelatorioService checkIfUserHasAccessToEditRelatorioService;
    private final GetRelatorioByIdExternoAndTenantIdService getRelatorioByIdExternoAndTenantIdService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final S3DeleteFile s3DeleteFile;
    private final GenerateRelatorioFileToUsersService generateRelatorioFileToUsersService;
    private final AssinaturaDeRelatorioRepository assinaturaDeRelatorioRepository;

    public void execute(Long id, String relatorioExternalId, String tenantExternalId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);
        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        RelatorioEntity relatorio = getRelatorioByIdExternoAndTenantIdService.execute(relatorioExternalId, tenantId);

        checkIfUserHasAccessToEditRelatorioService.execute(currentUserTenant, relatorio.getStatus());

        AssinaturaDeRelatorioEntity assinaturaDeRelatorioEntity = relatorio.getAssinaturas().stream()
                .filter(ass -> ass.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Assinatura de relatório não encontrado com id: " + id));

        String assinaturaUrl = assinaturaDeRelatorioEntity.getAssinaturaUrl();

        s3DeleteFile.execute(assinaturaUrl);

        assinaturaDeRelatorioEntity.setAssinaturaUrl(null);

        assinaturaDeRelatorioRepository.save(assinaturaDeRelatorioEntity);

        generateRelatorioFileToUsersService.execute(relatorio.getId());
    }
}
