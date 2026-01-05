package com.branches.arquivo.service;

import com.branches.arquivo.domain.ArquivoEntity;
import com.branches.arquivo.domain.enums.TipoArquivo;
import com.branches.arquivo.dto.request.CreateVideoDeRelatorioRequest;
import com.branches.arquivo.dto.response.CreateVideoDeRelatorioResponse;
import com.branches.arquivo.repository.ArquivoRepository;
import com.branches.exception.BadRequestException;
import com.branches.external.aws.S3UploadFile;
import com.branches.obra.controller.CheckIfUserHasAccessToObraService;
import com.branches.relatorio.domain.RelatorioEntity;
import com.branches.relatorio.repository.projections.RelatorioWithObraProjection;
import com.branches.relatorio.service.CheckIfUserHasAccessToEditRelatorioService;
import com.branches.relatorio.service.GetRelatorioWithObraByIdExternoAndTenantIdService;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import com.branches.utils.FileContentType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CreateVideoDeRelatorioService {
    private final S3UploadFile s3UploadFile;
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final CheckIfConfiguracaoDeRelatorioDaObraPermiteVideo checkIfConfiguracaoDeRelatorioDaObraPermiteVideo;
    private final CheckIfUserCanViewVideosService checkIfUserCanViewVideosService;
    private final GetRelatorioWithObraByIdExternoAndTenantIdService getRelatorioWithObraByIdExternoAndTenantIdService;
    private final ArquivoRepository arquivoRepository;

    private static final long MAX_VIDEO_SIZE_BYTES = 100 * 1024 * 1024; // 100 MB
    private final CheckIfUserHasAccessToEditRelatorioService checkIfUserHasAccessToEditRelatorioService;
    private final CheckIfUserHasAccessToObraService checkIfUserHasAccessToObraService;

    public CreateVideoDeRelatorioResponse execute(CreateVideoDeRelatorioRequest request, String tenantExternalId, String relatorioExternalId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        RelatorioWithObraProjection relatorioWithObra = getRelatorioWithObraByIdExternoAndTenantIdService.execute(relatorioExternalId, tenantId);
        RelatorioEntity relatorio = relatorioWithObra.getRelatorio();

        checkIfUserHasAccessToObraService.execute(currentUserTenant, relatorio.getObraId());
        checkIfConfiguracaoDeRelatorioDaObraPermiteVideo.execute(relatorioWithObra);
        checkIfUserCanViewVideosService.execute(currentUserTenant);
        checkIfUserHasAccessToEditRelatorioService.execute(currentUserTenant, relatorio.getStatus());

        byte[] videoBytes;
        try {
            String validBase64 = request.base64Video().replaceAll("^data:video/\\w+;base64,", "");
            videoBytes = Base64.getDecoder().decode(validBase64);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("O vídeo enviado não está em formato base64 válido");
        }

        if (videoBytes.length > MAX_VIDEO_SIZE_BYTES) {
            throw new BadRequestException("O tamanho do vídeo excede o limite de 100MB");
        }

        FileContentType contentType = getContentTypeFromString(request.contentType());

        String fileName = "%s-%s.%s".formatted(formatFileName(request.fileName()), LocalDateTime.now(), contentType.getExtension());
        String videoUrl = s3UploadFile.execute(
                fileName,
                "tenants/%s/obras/%s/relatorios/%s/videos".formatted(tenantExternalId, relatorioWithObra.getObra().getIdExterno(), relatorioExternalId),
                videoBytes,
                contentType
        );

        BigDecimal fileLengthInMb = BigDecimal.valueOf(videoBytes.length).divide(BigDecimal.valueOf(1024 * 1024), RoundingMode.HALF_UP);
        ArquivoEntity arquivo = ArquivoEntity.builder()
                .nomeArquivo(fileName)
                .url(videoUrl)
                .tipoArquivo(TipoArquivo.VIDEO)
                .relatorio(relatorio)
                .tamanhoEmMb(fileLengthInMb)
                .tenantId(tenantId)
                .build();

        ArquivoEntity saved = arquivoRepository.save(arquivo);

        return CreateVideoDeRelatorioResponse.from(saved);
    }

    private String formatFileName(String requestFileName) {
        return requestFileName.replaceAll("\\.[^.]+$", "").replaceAll("\\s+", "_");
    }

    private FileContentType getContentTypeFromString(String contentType) {
        return switch (contentType.toLowerCase()) {
            case "video/mp4", "mp4" -> FileContentType.MP4;
            case "video/x-msvideo", "avi" -> FileContentType.AVI;
            case "video/quicktime", "mov" -> FileContentType.MOV;
            default -> throw new BadRequestException("Tipo de vídeo não suportado. Use MP4, AVI ou MOV");
        };
    }
}

