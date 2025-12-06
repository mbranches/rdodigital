package com.branches.arquivo.service;

import com.branches.arquivo.domain.ArquivoEntity;
import com.branches.arquivo.domain.enums.TipoArquivo;
import com.branches.arquivo.dto.request.CreateFotoDeRelatorioRequest;
import com.branches.arquivo.dto.response.FotoDeRelatorioResponse;
import com.branches.arquivo.repository.ArquivoRepository;
import com.branches.external.aws.S3UploadFile;
import com.branches.relatorio.repository.projections.RelatorioWithObraProjection;
import com.branches.relatorio.service.CheckIfUserHasAccessToEditRelatorioService;
import com.branches.relatorio.service.GetRelatorioWithObraByIdExternoAndTenantIdService;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import com.branches.utils.CompressImage;
import com.branches.utils.FileContentType;
import com.branches.utils.ImageOutPutFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CreateFotoDeRelatorioService {
    private final CompressImage compressImage;
    private final S3UploadFile s3UploadFile;
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final CheckIfConfiguracaoDeRelatorioDaObraPermiteFoto checkIfConfiguracaoDeRelatorioDaObraPermiteFoto;
    private final CheckIfUserCanViewFotosService checkIfUserCanViewFotosService;
    private final GetRelatorioWithObraByIdExternoAndTenantIdService getRelatorioWithObraByIdExternoAndTenantIdService;
    private final ArquivoRepository arquivoRepository;
    private final CheckIfUserHasAccessToEditRelatorioService checkIfUserHasAccessToEditRelatorioService;

    public FotoDeRelatorioResponse execute(CreateFotoDeRelatorioRequest request, String tenantExternalId, String relatorioExternalId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        RelatorioWithObraProjection relatorioWithObra = getRelatorioWithObraByIdExternoAndTenantIdService.execute(relatorioExternalId, tenantId);

        checkIfConfiguracaoDeRelatorioDaObraPermiteFoto.execute(relatorioWithObra);
        checkIfUserCanViewFotosService.execute(currentUserTenant);
        checkIfUserHasAccessToEditRelatorioService.execute(currentUserTenant, relatorioWithObra.getRelatorio().getStatus());

        byte[] imageBytes = compressImage.execute(request.base64Image(), 800, 800, 0.8, ImageOutPutFormat.JPEG);

        String filename = "%s-%s".formatted(Instant.now(), request.fileName());
        String fotoUrl = s3UploadFile.execute(filename, "tenants/%s/obras/%s/relatorios/%s/fotos".formatted(tenantExternalId, relatorioWithObra.getObra().getIdExterno(), relatorioExternalId), imageBytes, FileContentType.JPEG);

        BigDecimal fileLengthInMb = BigDecimal.valueOf(imageBytes.length).divide(BigDecimal.valueOf(1024 * 1024), RoundingMode.HALF_UP);
        ArquivoEntity arquivo = ArquivoEntity.builder()
                .descricao(request.descricao())
                .nomeArquivo(filename)
                .url(fotoUrl)
                .tipoArquivo(TipoArquivo.FOTO)
                .relatorio(relatorioWithObra.getRelatorio())
                .tamanhoEmMb(fileLengthInMb)
                .tenantId(tenantId)
                .build();

        ArquivoEntity saved = arquivoRepository.save(arquivo);

        return FotoDeRelatorioResponse.from(saved);
    }
}
