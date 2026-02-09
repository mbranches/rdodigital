package com.branches.relatorio.service;

import com.branches.arquivo.domain.ArquivoEntity;
import com.branches.arquivo.repository.ArquivoRepository;
import com.branches.atividade.domain.AtividadeDeRelatorioEntity;
import com.branches.atividade.repository.AtividadeDeRelatorioRepository;
import com.branches.comentarios.model.ComentarioDeRelatorioEntity;
import com.branches.comentarios.repository.ComentarioDeRelatorioRepository;
import com.branches.equipamento.domain.EquipamentoDeRelatorioEntity;
import com.branches.equipamento.repository.EquipamentoDeRelatorioRepository;
import com.branches.exception.NotFoundException;
import com.branches.maodeobra.domain.MaoDeObraDeRelatorioEntity;
import com.branches.maodeobra.repository.MaoDeObraDeRelatorioRepository;
import com.branches.material.domain.MaterialDeRelatorioEntity;
import com.branches.material.repository.MaterialDeRelatorioRepository;
import com.branches.obra.controller.CheckIfUserHasAccessToObraService;
import com.branches.ocorrencia.domain.OcorrenciaDeRelatorioEntity;
import com.branches.ocorrencia.repository.OcorrenciaDeRelatorioRepository;
import com.branches.relatorio.domain.AssinaturaDeRelatorioEntity;
import com.branches.relatorio.dto.response.ImprimirRelatorioResponse;
import com.branches.relatorio.repository.AssinaturaDeRelatorioRepository;
import com.branches.relatorio.repository.RelatorioRepository;
import com.branches.relatorio.repository.projections.RelatorioDetailsProjection;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ImprimirRelatorioService {
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final RelatorioRepository relatorioRepository;
    private final AssinaturaDeRelatorioRepository assinaturaDeRelatorioRepository;
    private final GenerateRelatorioFileService generateRelatorioFileService;
    private final ArquivoRepository arquivoRepository;
    private final OcorrenciaDeRelatorioRepository ocorrenciaDeRelatorioRepository;
    private final AtividadeDeRelatorioRepository atividadeDeRelatorioRepository;
    private final EquipamentoDeRelatorioRepository equipamentoDeRelatorioRepository;
    private final MaoDeObraDeRelatorioRepository maoDeObraDeRelatorioRepository;
    private final ComentarioDeRelatorioRepository comentarioDeRelatorioRepository;
    private final MaterialDeRelatorioRepository materialDeRelatorioRepository;
    private final CheckIfUserHasAccessToObraService checkIfUserHasAccessToObraService;

    public ImprimirRelatorioResponse execute(String relatorioExternalId, String tenantExternalId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        var userPermissionsItensDeRelatorio = currentUserTenant.getAuthorities().getItensDeRelatorio();

        boolean userCanViewOcorrencias = userPermissionsItensDeRelatorio.getOcorrencias();
        boolean userCanViewAtividades = userPermissionsItensDeRelatorio.getAtividades();
        boolean userCanViewEquipamentos = userPermissionsItensDeRelatorio.getEquipamentos();
        boolean userCanViewMaoDeObra = userPermissionsItensDeRelatorio.getMaoDeObra();
        boolean userCanViewComentarios = userPermissionsItensDeRelatorio.getComentarios();
        boolean userCanViewMateriais = userPermissionsItensDeRelatorio.getMateriais();
        boolean userCanViewFotos = userPermissionsItensDeRelatorio.getFotos();
        boolean userCanViewVideos = userPermissionsItensDeRelatorio.getVideos();

        RelatorioDetailsProjection relatorioDetails = relatorioRepository.findDetailsByIdExternoAndTenantId(relatorioExternalId, tenantId)
                .orElseThrow(() -> new NotFoundException("Relatório não encontrado com o id: " + relatorioExternalId));

        checkIfUserHasAccessToObraService.execute(currentUserTenant, relatorioDetails.getObraId());

        List<OcorrenciaDeRelatorioEntity> ocorrenciasDoRelatorio = fetchOcorrenciasIfAllowed(relatorioDetails, userCanViewOcorrencias);
        List<AtividadeDeRelatorioEntity> atividadesDoRelatorio = fetchAtividadesIfAllowed(relatorioDetails, userCanViewAtividades);
        List<EquipamentoDeRelatorioEntity> equipamentosDoRelatorio = fetchEquipamentosIfAllowed(relatorioDetails, userCanViewEquipamentos);
        List<MaoDeObraDeRelatorioEntity> maoDeObraDoRelatorio = fetchMaoDeObraIfAllowed(relatorioDetails, userCanViewMaoDeObra);
        List<ComentarioDeRelatorioEntity> comentariosDoRelatorio = fetchComentariosIfAllowed(relatorioDetails, userCanViewComentarios);
        List<MaterialDeRelatorioEntity> materiaisDoRelatorio = fetchMateriaisIfAllowed(relatorioDetails, userCanViewMateriais);
        List<ArquivoEntity> arquivosDoRelatorio = fetchArquivos(relatorioDetails);
        List<ArquivoEntity> fotosDoRelatorio = userCanViewFotos ? arquivosDoRelatorio.stream().filter(ArquivoEntity::getIsFoto).toList() : Collections.emptyList();
        List<ArquivoEntity> videosDoRelatorio = userCanViewVideos ? arquivosDoRelatorio.stream().filter(ArquivoEntity::getIsVideo).toList() : Collections.emptyList();
        List<AssinaturaDeRelatorioEntity> assinaturasDoRelatorio = assinaturaDeRelatorioRepository.findAllByRelatorioIdOrderByEnversCreatedDate(relatorioDetails.getId());

        String url = generateRelatorioFileService.execute(
                relatorioDetails,
                currentUserTenant,
                ocorrenciasDoRelatorio,
                atividadesDoRelatorio,
                equipamentosDoRelatorio,
                maoDeObraDoRelatorio,
                comentariosDoRelatorio,
                materiaisDoRelatorio,
                fotosDoRelatorio,
                videosDoRelatorio,
                assinaturasDoRelatorio
        );

        return ImprimirRelatorioResponse.from(url);
    }

    private List<OcorrenciaDeRelatorioEntity> fetchOcorrenciasIfAllowed(RelatorioDetailsProjection relatorioDetails, boolean userCanViewOcorrencias) {
        if (relatorioDetails.getShowOcorrencias() && userCanViewOcorrencias) {
            return ocorrenciaDeRelatorioRepository.findAllByRelatorioId(relatorioDetails.getId());
        }
        return Collections.emptyList();
    }

    private List<AtividadeDeRelatorioEntity> fetchAtividadesIfAllowed(RelatorioDetailsProjection relatorioDetails, boolean userCanViewAtividades) {
        if (relatorioDetails.getShowAtividades() && userCanViewAtividades) {
            return atividadeDeRelatorioRepository.findAllByRelatorioId(relatorioDetails.getId());
        }
        return Collections.emptyList();
    }

    private List<EquipamentoDeRelatorioEntity> fetchEquipamentosIfAllowed(RelatorioDetailsProjection relatorioDetails, boolean userCanViewEquipamentos) {
        if (relatorioDetails.getShowEquipamentos() && userCanViewEquipamentos) {
            return equipamentoDeRelatorioRepository.findAllByRelatorioId(relatorioDetails.getId());
        }
        return Collections.emptyList();
    }

    private List<MaoDeObraDeRelatorioEntity> fetchMaoDeObraIfAllowed(RelatorioDetailsProjection relatorioDetails, boolean userCanViewMaoDeObra) {
        if (relatorioDetails.getShowMaoDeObra() && userCanViewMaoDeObra) {
            return maoDeObraDeRelatorioRepository.findAllByRelatorioId(relatorioDetails.getId());
        }
        return Collections.emptyList();
    }

    private List<ComentarioDeRelatorioEntity> fetchComentariosIfAllowed(RelatorioDetailsProjection relatorioDetails, boolean userCanViewComentarios) {
        if (relatorioDetails.getShowComentarios() && userCanViewComentarios) {
            return comentarioDeRelatorioRepository.findAllByRelatorioId(relatorioDetails.getId());
        }
        return Collections.emptyList();
    }

    private List<MaterialDeRelatorioEntity> fetchMateriaisIfAllowed(RelatorioDetailsProjection relatorioDetails, boolean userCanViewMateriais) {
        if (relatorioDetails.getShowMateriais() && userCanViewMateriais) {
            return materialDeRelatorioRepository.findAllByRelatorioId(relatorioDetails.getId());
        }
        return Collections.emptyList();
    }

    private List<ArquivoEntity> fetchArquivos(RelatorioDetailsProjection relatorioDetails) {
        if (relatorioDetails.getShowFotos() || relatorioDetails.getShowVideos()) {
            return arquivoRepository.findAllByRelatorioIdOrderByEnversCreatedDateDesc(relatorioDetails.getId());
        }
        return Collections.emptyList();
    }
}
