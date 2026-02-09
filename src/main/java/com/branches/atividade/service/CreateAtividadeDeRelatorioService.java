package com.branches.atividade.service;

import com.branches.atividade.domain.AtividadeDeRelatorioCampoPersonalizadoEntity;
import com.branches.atividade.domain.AtividadeDeRelatorioEntity;
import com.branches.atividade.domain.FotoDeAtividadeEntity;
import com.branches.atividade.dto.request.CreateAtividadeDeRelatorioRequest;
import com.branches.atividade.dto.request.CreateFotoDeAtividadeRequest;
import com.branches.atividade.dto.response.CreateAtividadeDeRelatorioResponse;
import com.branches.atividade.repository.AtividadeDeRelatorioRepository;
import com.branches.external.aws.S3UploadFile;
import com.branches.maodeobra.domain.MaoDeObraDeAtividadeDeRelatorioEntity;
import com.branches.maodeobra.domain.MaoDeObraEntity;
import com.branches.maodeobra.repository.MaoDeObraDeAtividadeDeRelatorioRepository;
import com.branches.maodeobra.service.GetMaoDeObraListByIdInAndTenantIdAndTypeService;
import com.branches.obra.controller.CheckIfUserHasAccessToObraService;
import com.branches.obra.domain.ObraEntity;
import com.branches.obra.domain.enums.TipoMaoDeObra;
import com.branches.obra.service.GetObraByIdAndTenantIdService;
import com.branches.relatorio.domain.RelatorioEntity;
import com.branches.relatorio.dto.request.CampoPersonalizadoRequest;
import com.branches.relatorio.service.CheckIfUserHasAccessToEditRelatorioService;
import com.branches.relatorio.service.GetRelatorioByIdExternoAndTenantIdService;
import com.branches.tenant.domain.TenantEntity;
import com.branches.tenant.service.GetTenantByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import com.branches.utils.CalculateMinutosTotais;
import com.branches.utils.CompressImage;
import com.branches.utils.FileContentType;
import com.branches.utils.ImageOutPutFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class CreateAtividadeDeRelatorioService {
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final CheckIfUserHasAccessToEditRelatorioService checkIfUserHasAccessToEditRelatorioService;
    private final GetRelatorioByIdExternoAndTenantIdService getRelatorioByIdExternoAndTenantIdService;
    private final CheckIfConfiguracaoDeRelatorioDaObraPermiteAtividade checkIfConfiguracaoDeRelatorioDaObraPermiteAtividade;
    private final CheckIfUserCanViewAtividadesService checkIfUserCanViewAtividadesService;
    private final GetMaoDeObraListByIdInAndTenantIdAndTypeService getMaoDeObraListByIdInAndTenantIdAndTypeService;
    private final CalculateMinutosTotais calculateMinutosTotais;
    private final AtividadeDeRelatorioRepository atividadeDeRelatorioRepository;
    private final MaoDeObraDeAtividadeDeRelatorioRepository maoDeObraDeAtividadeDeRelatorioRepository;
    private final CheckIfUserHasAccessToObraService checkIfUserHasAccessToObraService;
    private final GetObraByIdAndTenantIdService getObraByIdAndTenantIdService;
    private final S3UploadFile s3UploadFile;
    private final CompressImage compressImage;
    private final GetTenantByIdExternoService getTenantByIdExternoService;

    public CreateAtividadeDeRelatorioResponse execute(CreateAtividadeDeRelatorioRequest request, String relatorioExternalId, String tenantExternalId, List<UserTenantEntity> userTenants) {
        TenantEntity tenant = getTenantByIdExternoService.execute(tenantExternalId);
        Long tenantId = tenant.getId();

        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        RelatorioEntity relatorio = getRelatorioByIdExternoAndTenantIdService.execute(relatorioExternalId, tenantId);
        ObraEntity obra = getObraByIdAndTenantIdService.execute(relatorio.getObraId(), tenantId);
        checkIfUserHasAccessToObraService.execute(currentUserTenant, relatorio.getObraId());
        checkIfUserHasAccessToEditRelatorioService.execute(currentUserTenant, relatorio.getStatus());
        checkIfConfiguracaoDeRelatorioDaObraPermiteAtividade.execute(obra);
        checkIfUserCanViewAtividadesService.execute(currentUserTenant);

        List<MaoDeObraEntity> maoDeObraEntities = getMaoDeObraDaAtividade(request, tenantId, relatorio.getTipoMaoDeObra());


        AtividadeDeRelatorioEntity atividadeDeRelatorio = AtividadeDeRelatorioEntity.builder()
                .relatorio(relatorio)
                .descricao(request.descricao())
                .quantidadeRealizada(request.quantidadeRealizada())
                .unidadeMedida(request.unidadeMedida())
                .porcentagemConcluida(request.porcentagemConcluida())
                .horaInicio(request.horaInicio())
                .horaFim(request.horaFim())
                .minutosTotais(calculateMinutosTotais.execute(request.horaInicio(), request.horaFim(), null))
                .status(request.status())
                .tenantId(tenantId)
                .build();

        List<AtividadeDeRelatorioCampoPersonalizadoEntity> camposPersonalizados = getCamposPersonalizadosToSave(request.camposPersonalizados(), atividadeDeRelatorio, tenantId);
        atividadeDeRelatorio.setCamposPersonalizados(camposPersonalizados);

        if (request.fotos() != null && !request.fotos().isEmpty()) {
            List<FotoDeAtividadeEntity> fotos = request.fotos().stream()
                    .map(fotoRequest -> createFotoDeAtividadeEntity(fotoRequest, atividadeDeRelatorio, tenant, obra))
                    .toList();

            atividadeDeRelatorio.setFotos(fotos);
        }

        AtividadeDeRelatorioEntity saved = atividadeDeRelatorioRepository.save(atividadeDeRelatorio);

        List<MaoDeObraDeAtividadeDeRelatorioEntity> maoDeObra = saveMaoDeObraDeAtividade(saved, maoDeObraEntities, tenantId);
        saved.setMaoDeObra(maoDeObra);

        return CreateAtividadeDeRelatorioResponse.from(saved);
    }

    private FotoDeAtividadeEntity createFotoDeAtividadeEntity(CreateFotoDeAtividadeRequest fotoRequest, AtividadeDeRelatorioEntity atividadeDeRelatorio, TenantEntity tenant, ObraEntity obraEntity) {
        String fileUrl = processAndUploadFile(fotoRequest, atividadeDeRelatorio.getId(), obraEntity.getIdExterno(), tenant.getIdExterno(), atividadeDeRelatorio.getRelatorio().getIdExterno());

        return FotoDeAtividadeEntity.builder()
                .tenantId(tenant.getId())
                .atividade(atividadeDeRelatorio)
                .url(fileUrl)
                .filename(fotoRequest.fileName())
                .build();
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

    private List<AtividadeDeRelatorioCampoPersonalizadoEntity> getCamposPersonalizadosToSave(List<CampoPersonalizadoRequest> requestList, AtividadeDeRelatorioEntity toSave, Long tenantId) {
        if (requestList == null || requestList.isEmpty()) {
            return List.of();
        }

        return requestList.stream()
                .map(request -> request.toEntity(tenantId))
                .map(campo -> AtividadeDeRelatorioCampoPersonalizadoEntity.from(toSave, campo, tenantId))
                .toList();
    }

    private List<MaoDeObraEntity> getMaoDeObraDaAtividade(CreateAtividadeDeRelatorioRequest request, Long tenantId, TipoMaoDeObra tipoMaoDeObra) {
        if (request.maoDeObraIds() == null) return Collections.emptyList();

        HashSet<Long> maoDeObraIds = new HashSet<>(request.maoDeObraIds());

        if (maoDeObraIds.isEmpty()) {
            return Collections.emptyList();
        }

        return getMaoDeObraListByIdInAndTenantIdAndTypeService.execute(maoDeObraIds, tenantId, tipoMaoDeObra);
    }

    private List<MaoDeObraDeAtividadeDeRelatorioEntity> saveMaoDeObraDeAtividade(AtividadeDeRelatorioEntity atividade, List<MaoDeObraEntity> maoDeObraEntities, Long tenantId) {
        List<MaoDeObraDeAtividadeDeRelatorioEntity> toSave = maoDeObraEntities.stream()
                .map(m -> MaoDeObraDeAtividadeDeRelatorioEntity.builder()
                        .atividadeDeRelatorio(atividade)
                        .maoDeObra(m)
                        .funcao(m.getFuncao())
                        .tenantId(tenantId)
                        .build())
                .collect(Collectors.toList());

        return maoDeObraDeAtividadeDeRelatorioRepository.saveAll(toSave);
    }
}
