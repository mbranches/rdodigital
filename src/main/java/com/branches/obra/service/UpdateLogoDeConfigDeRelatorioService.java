package com.branches.obra.service;

import com.branches.exception.BadRequestException;
import com.branches.external.aws.S3UploadFile;
import com.branches.obra.controller.enums.TipoLogoDeConfiguracaoDeRelatorio;
import com.branches.obra.domain.ConfiguracaoRelatoriosEntity;
import com.branches.obra.domain.LogoDeRelatorioEntity;
import com.branches.obra.domain.ObraEntity;
import com.branches.relatorio.dto.request.UpdateLogoDeConfigDeRelatorioRequest;
import com.branches.relatorio.repository.LogoDeRelatorioRepository;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import com.branches.utils.CompressImage;
import com.branches.utils.FileContentType;
import com.branches.utils.ImageOutPutFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UpdateLogoDeConfigDeRelatorioService {
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final GetObraByIdExternoAndTenantIdService getObraByIdExternoAndTenantIdService;
    private final CheckIfUserCanEditObraService checkIfUserCanEditObraService;
    private final CompressImage compressImage;
    private final S3UploadFile s3UploadFile;
    private final LogoDeRelatorioRepository logoDeRelatorioRepository;

    public void execute(UpdateLogoDeConfigDeRelatorioRequest request, TipoLogoDeConfiguracaoDeRelatorio tipoLogo, String obraExternalId, String tenantExternalId, List<UserTenantEntity> userTenants) {
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

        byte[] bytes = compressImage.execute(request.logoBase64(), 1000, 400, 0.7, ImageOutPutFormat.PNG);

        String path = "tenants/%s/obras/%s/configuracao-relatorios/logos".formatted(tenantExternalId, obraExternalId);
        String fileName = "logo-relatorio-%s-%s.png".formatted(tipoLogo.name().toLowerCase(), obraExternalId);

        String logoUrl = s3UploadFile.execute(fileName, path, bytes, FileContentType.PNG);

        logoToEdit.setExibir(request.exibir());
        logoToEdit.setUrl(logoUrl);

        logoDeRelatorioRepository.save(logoToEdit);
    }
}
