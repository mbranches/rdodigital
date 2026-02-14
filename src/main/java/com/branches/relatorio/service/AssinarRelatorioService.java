package com.branches.relatorio.service;

import com.branches.exception.NotFoundException;
import com.branches.external.aws.S3UploadFile;
import com.branches.obra.service.CheckIfUserHasAccessToObraService;
import com.branches.obra.domain.ObraEntity;
import com.branches.relatorio.domain.AssinaturaDeRelatorioEntity;
import com.branches.relatorio.domain.RelatorioEntity;
import com.branches.relatorio.dto.request.AssinarRelatorioRequest;
import com.branches.relatorio.repository.AssinaturaDeRelatorioRepository;
import com.branches.relatorio.repository.projections.RelatorioWithObraProjection;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import com.branches.utils.CompressImage;
import com.branches.utils.FileContentType;
import com.branches.utils.ImageOutPutFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class AssinarRelatorioService {
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final CheckIfUserHasAccessToEditRelatorioService checkIfUserHasAccessToEditRelatorioService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final S3UploadFile s3UploadFile;
    private final CompressImage compressImage;
    private final AssinaturaDeRelatorioRepository assinaturaDeRelatorioRepository;
    private final CheckIfUserHasAccessToObraService checkIfUserHasAccessToObraService;
    private final GetRelatorioWithObraByIdExternoAndTenantIdService getRelatorioWithObraByIdExternoAndTenantIdService;

    public void execute(AssinarRelatorioRequest request, Long id, String relatorioExternalId, String tenantExternalId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);
        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        RelatorioWithObraProjection relatorioWithObraProjection = getRelatorioWithObraByIdExternoAndTenantIdService.execute(relatorioExternalId, tenantId);
        RelatorioEntity relatorio = relatorioWithObraProjection.getRelatorio();
        ObraEntity obra = relatorioWithObraProjection.getObra();

        checkIfUserHasAccessToObraService.execute(currentUserTenant, relatorio.getObraId());
        checkIfUserHasAccessToEditRelatorioService.execute(currentUserTenant, relatorio.getStatus());

        AssinaturaDeRelatorioEntity assinaturaDeRelatorioEntity = relatorio.getAssinaturas().stream()
                .filter(ass -> ass.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Assinatura de relatório não encontrado com id: " + id));

        byte[] signatureBytes = compressImage.execute(request.base64Image(), 400, 400, 0.8, ImageOutPutFormat.PNG);

        String fileName = "signature-%s-%s-%s.png".formatted(assinaturaDeRelatorioEntity.getConfiguracao().getNomeAssinante(), assinaturaDeRelatorioEntity.getId(), LocalDateTime.now());
        String path = "tenants/%s/obras/%s/relatorios/%s/assinaturas".formatted(tenantExternalId, obra.getIdExterno(), relatorio.getIdExterno());

        String signatureUrl = s3UploadFile.execute(fileName, path, signatureBytes, FileContentType.PNG);

        assinaturaDeRelatorioEntity.setAssinaturaUrl(signatureUrl);

        assinaturaDeRelatorioRepository.save(assinaturaDeRelatorioEntity);
    }
}
