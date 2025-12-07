package com.branches.relatorio.service;

import com.branches.exception.NotFoundException;
import com.branches.external.aws.S3UploadFile;
import com.branches.relatorio.domain.AssinaturaDeRelatorioEntity;
import com.branches.relatorio.domain.RelatorioEntity;
import com.branches.relatorio.dto.request.AssinarRelatorioRequest;
import com.branches.relatorio.repository.AssinaturaDeRelatorioRepository;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import com.branches.utils.CompressImage;
import com.branches.utils.FileContentType;
import com.branches.utils.ImageOutPutFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class AssinarRelatorioService {
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final CheckIfUserHasAccessToEditRelatorioService checkIfUserHasAccessToEditRelatorioService;
    private final GetRelatorioByIdExternoAndTenantIdService getRelatorioByIdExternoAndTenantIdService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final S3UploadFile s3UploadFile;
    private final CompressImage compressImage;
    private final GenerateRelatorioFileToUsersService generateRelatorioFileToUsersService;
    private final AssinaturaDeRelatorioRepository assinaturaDeRelatorioRepository;

    public void execute(AssinarRelatorioRequest request, Long id, String relatorioExternalId, String tenantExternalId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);
        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        RelatorioEntity relatorio = getRelatorioByIdExternoAndTenantIdService.execute(relatorioExternalId, tenantId);

        checkIfUserHasAccessToEditRelatorioService.execute(currentUserTenant, relatorio.getStatus());

        AssinaturaDeRelatorioEntity assinaturaDeRelatorioEntity = relatorio.getAssinaturas().stream()
                .filter(ass -> ass.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Assinatura de relatório não encontrado com id: " + id));

        byte[] signatureBytes = compressImage.execute(request.base64Signature(), 400, 400, 0.8, ImageOutPutFormat.PNG);

        String fileName = "signature-%s-%s.png".formatted(assinaturaDeRelatorioEntity.getConfiguracao().getNomeAssinante(), relatorio.getIdExterno());
        String path = "tenants/%s/obras/%s/relatorios/%s/assinaturas";

        String signatureUrl = s3UploadFile.execute(fileName, path, signatureBytes, FileContentType.PNG);

        assinaturaDeRelatorioEntity.setAssinaturaUrl(signatureUrl);

        assinaturaDeRelatorioRepository.save(assinaturaDeRelatorioEntity);

        generateRelatorioFileToUsersService.execute(relatorio.getId());
    }
}
