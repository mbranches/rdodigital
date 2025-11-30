package com.branches.obra.service;

import com.branches.exception.BadRequestException;
import com.branches.obra.controller.enums.TipoLogoDeConfiguracaoDeRelatorio;
import com.branches.obra.domain.ConfiguracaoRelatoriosEntity;
import com.branches.obra.domain.LogoDeRelatorioEntity;
import com.branches.obra.domain.ObraEntity;
import com.branches.relatorio.repository.LogoDeRelatorioRepository;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RemoveLogoDeConfigDeRelatorioService {

    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final GetObraByIdExternoAndTenantIdService getObraByIdExternoAndTenantIdService;
    private final CheckIfUserCanEditObraService checkIfUserCanEditObraService;
    private final LogoDeRelatorioRepository logoDeRelatorioRepository;

    public void execute(TipoLogoDeConfiguracaoDeRelatorio tipoLogo, String obraExternalId, String tenantExternalId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);
        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        ObraEntity obra = getObraByIdExternoAndTenantIdService.execute(obraExternalId, tenantId);

        checkIfUserCanEditObraService.execute(currentUserTenant, obra.getId());

        ConfiguracaoRelatoriosEntity configuracaoRelatorios = obra.getConfiguracaoRelatorios();

        LogoDeRelatorioEntity logoToEdit;
        switch (tipoLogo) {
            case LOGO_DOIS -> logoToEdit = configuracaoRelatorios.getLogoDeRelatorio2();
            case LOGO_TRES -> logoToEdit = configuracaoRelatorios.getLogoDeRelatorio3();
            default -> throw new BadRequestException("Tipo de logo inválido para edição");
        }

        logoToEdit.setUrl(null);
        logoToEdit.setExibir(false);

        logoDeRelatorioRepository.save(logoToEdit);
    }
}
