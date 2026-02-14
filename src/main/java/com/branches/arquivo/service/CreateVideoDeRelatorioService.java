package com.branches.arquivo.service;

import com.branches.arquivo.domain.ArquivoEntity;
import com.branches.arquivo.domain.enums.TipoArquivo;
import com.branches.arquivo.dto.response.CreateVideoDeRelatorioResponse;
import com.branches.arquivo.repository.ArquivoRepository;
import com.branches.exception.BadRequestException;
import com.branches.exception.InternalServerError;
import com.branches.external.aws.S3UploadFile;
import com.branches.obra.service.CheckIfUserHasAccessToObraService;
import com.branches.relatorio.domain.RelatorioEntity;
import com.branches.relatorio.repository.projections.RelatorioWithObraProjection;
import com.branches.relatorio.service.CheckIfUserHasAccessToEditRelatorioService;
import com.branches.relatorio.service.GetRelatorioWithObraByIdExternoAndTenantIdService;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import com.branches.utils.FileContentType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
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

    public CreateVideoDeRelatorioResponse execute(MultipartFile video, String descricao, String tenantExternalId, String relatorioExternalId, List<UserTenantEntity> userTenants) {
        log.info("Iniciando upload de vídeo para o relatório: {} do tenant: {}", relatorioExternalId, tenantExternalId);
        if (video == null || video.isEmpty()) {
            throw new BadRequestException("O vídeo é obrigatório");
        }

        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        RelatorioWithObraProjection relatorioWithObra = getRelatorioWithObraByIdExternoAndTenantIdService.execute(relatorioExternalId, tenantId);
        RelatorioEntity relatorio = relatorioWithObra.getRelatorio();

        checkIfUserHasAccessToObraService.execute(currentUserTenant, relatorio.getObraId());
        checkIfConfiguracaoDeRelatorioDaObraPermiteVideo.execute(relatorioWithObra.getObra());
        checkIfUserCanViewVideosService.execute(currentUserTenant);
        checkIfUserHasAccessToEditRelatorioService.execute(currentUserTenant, relatorio.getStatus());

        byte[] videoBytes;
        try {
            videoBytes = video.getBytes();
        } catch (IOException e) {
            throw new BadRequestException("Erro ao ler o arquivo de vídeo");
        }

        if (videoBytes.length > MAX_VIDEO_SIZE_BYTES) {
            throw new BadRequestException("O tamanho do vídeo excede o limite de 100MB");
        }

        String originalFilename = video.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new BadRequestException("Nome do arquivo é obrigatório");
        }

        String contentTypeStr = video.getContentType();
        if (contentTypeStr == null || contentTypeStr.isEmpty()) {
            throw new BadRequestException("Tipo de conteúdo é obrigatório");
        }
        FileContentType contentType = getContentTypeFromString(contentTypeStr);

        BigDecimal durationInSeconds = getVideoDurationInSeconds(video);
        if (durationInSeconds.compareTo(BigDecimal.valueOf(60)) > 0) {
            throw new BadRequestException("A duração do vídeo não pode exceder 60 segundos");
        }

        String fileName = "%s-%s.%s".formatted(formatFileName(originalFilename), LocalDateTime.now(), contentType.getExtension());
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
                .descricao(descricao)
                .tipoArquivo(TipoArquivo.VIDEO)
                .relatorio(relatorio)
                .tamanhoEmMb(fileLengthInMb)
                .segundosDeDuracao(durationInSeconds)
                .tenantId(tenantId)
                .build();

        ArquivoEntity saved = arquivoRepository.save(arquivo);

        return CreateVideoDeRelatorioResponse.from(saved);
    }

    public static BigDecimal getVideoDurationInSeconds(MultipartFile file) {
        try {
            Path tempFile = Files.createTempFile("video-", ".tmp");
            file.transferTo(tempFile.toFile());

            ProcessBuilder pb = new ProcessBuilder(
                    "ffprobe",
                    "-v", "error",
                    "-show_entries", "format=duration",
                    "-of", "default=noprint_wrappers=1:nokey=1",
                    tempFile.toAbsolutePath().toString()
            );

            Process process = pb.start();
            String output = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            ).readLine();

            Files.delete(tempFile);

            return new BigDecimal(output);

        } catch (Exception e) {
            log.error("Erro ao obter duração do vídeo: {}", e.getMessage());
            throw new InternalServerError("Erro ao obter duração do vídeo");
        }
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

