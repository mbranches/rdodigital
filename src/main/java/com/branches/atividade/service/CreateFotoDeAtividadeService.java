package com.branches.atividade.service;

import com.branches.atividade.domain.AtividadeDeRelatorioEntity;
import com.branches.atividade.domain.FotoDeAtividadeEntity;
import com.branches.atividade.repository.FotoDeAtividadeRepository;
import com.branches.atividade.dto.request.CreateFotoDeAtividadeRequest;
import com.branches.atividade.dto.response.FotoDeAtividadeResponse;
import com.branches.external.aws.S3UploadFile;
import com.branches.obra.domain.ObraEntity;
import com.branches.obra.service.GetObraByIdAndTenantIdService;
import com.branches.relatorio.domain.RelatorioEntity;
import com.branches.relatorio.service.GetRelatorioByIdExternoAndTenantIdService;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import com.branches.utils.CompressImage;
import com.branches.utils.FileContentType;
import com.branches.utils.ImageOutPutFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CreateFotoDeAtividadeService {

    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final CheckIfUserCanViewAtividadesService checkIfUserCanViewAtividadesService;
    private final CheckIfConfiguracaoDeRelatorioDaObraPermiteAtividade checkIfConfiguracaoDeRelatorioDaObraPermiteAtividade;
    private final GetAtividadeDeRelatorioByIdAndRelatorioIdService getAtividadeDeRelatorioByIdAndRelatorioIdService;
    private final GetRelatorioByIdExternoAndTenantIdService getRelatorioByIdExternoAndTenantIdService;
    private final FotoDeAtividadeRepository fotoDeAtividadeRepository;
    private final CompressImage compressImage;
    private final S3UploadFile s3UploadFile;
    private final GetObraByIdAndTenantIdService getObraByIdAndTenantIdService;

    public FotoDeAtividadeResponse execute(String tenantExternalId, String relatorioExternalId, Long atividadeId, CreateFotoDeAtividadeRequest request, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        RelatorioEntity relatorio = getRelatorioByIdExternoAndTenantIdService.execute(relatorioExternalId, tenantId);

        AtividadeDeRelatorioEntity atividade = getAtividadeDeRelatorioByIdAndRelatorioIdService.execute(atividadeId, relatorio.getId());

        checkIfUserCanViewAtividadesService.execute(currentUserTenant);
        ObraEntity obra = getObraByIdAndTenantIdService.execute(relatorio.getObraId(), tenantId);
        checkIfConfiguracaoDeRelatorioDaObraPermiteAtividade.execute(obra);

        String fileUrl = processAndUploadFile(request, atividade.getId(), obra.getIdExterno(), tenantExternalId, relatorioExternalId);

        FotoDeAtividadeEntity fotoDeAtividade = FotoDeAtividadeEntity.builder()
                .url(fileUrl)
                .filename(request.fileName())
                .tenantId(tenantId)
                .atividade(atividade)
                .build();

        FotoDeAtividadeEntity saved = fotoDeAtividadeRepository.save(fotoDeAtividade);

        return FotoDeAtividadeResponse.from(saved);
    }

    private String processAndUploadFile(CreateFotoDeAtividadeRequest request, Long atividadeId, String obraExternalId, String tenantExternalId, String relatorioExternalId) {
        byte[] imageBytes = processImage(request.base64Image());

        String path = "tenants/%s/obras/%s/relatorios/%s/atividades/%d/fotos"
                .formatted(
                        tenantExternalId,
                        obraExternalId,
                        relatorioExternalId,
                        atividadeId
                );
        String fileName = request.fileName().replaceAll("\\.[^.]+$", "") + "-" + LocalDateTime.from(LocalDateTime.now().atOffset(ZoneOffset.of("-03:00")));

        return s3UploadFile.execute(fileName, path, imageBytes, FileContentType.JPEG);
    }

    private byte[] processImage(String base64) {
        return compressImage.execute(base64, 800, 800, 0.8, ImageOutPutFormat.JPEG);
    }
}
