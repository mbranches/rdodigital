package com.branches.tenant.service;

import com.branches.exception.ForbiddenException;
import com.branches.obra.domain.LogoDeRelatorioEntity;
import com.branches.relatorio.repository.LogoDeRelatorioRepository;
import com.branches.tenant.domain.TenantEntity;
import com.branches.tenant.repository.TenantRepository;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.domain.enums.PerfilUserTenant;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RemoveTenantLogoService {
    private final TenantRepository tenantRepository;
    private final LogoDeRelatorioRepository logoDeRelatorioRepository;
    private final GetTenantByIdExternoService getTenantByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;

    public void execute(String tenantExternalId, List<UserTenantEntity> userTenants) {
        TenantEntity tenant = getTenantByIdExternoService.execute(tenantExternalId);

        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenant.getId());

        checkIfUserCanUpdateTenantLogo(currentUserTenant);
        tenant.setLogoUrl(null);

        tenantRepository.save(tenant);

        updateLogoDoTenantDeConfigDeRelatorio(tenant.getId());
    }

    private void updateLogoDoTenantDeConfigDeRelatorio(Long id) {
        List<LogoDeRelatorioEntity> logos = logoDeRelatorioRepository.findAllByTenantIdAndIsLogoDoTenantIsTrue(id);

        List<LogoDeRelatorioEntity> logosToSave = logos.stream()
                .peek(logo -> {
                    logo.setUrl(null);
                    logo.setExibir(false);
                })
                .toList();

        logoDeRelatorioRepository.saveAll(logosToSave);
    }

    private void checkIfUserCanUpdateTenantLogo(UserTenantEntity currentUserTenant) {
        if (currentUserTenant.getPerfil().equals(PerfilUserTenant.ADMINISTRADOR)) return;

        throw new ForbiddenException();
    }
}
