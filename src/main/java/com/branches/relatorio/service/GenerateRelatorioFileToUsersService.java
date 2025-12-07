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
import com.branches.ocorrencia.domain.OcorrenciaDeRelatorioEntity;
import com.branches.ocorrencia.repository.OcorrenciaDeRelatorioRepository;
import com.branches.relatorio.domain.ArquivoDeRelatorioDeUsuarioEntity;
import com.branches.relatorio.domain.AssinaturaDeRelatorioEntity;
import com.branches.relatorio.repository.ArquivoDeRelatorioDeUsuarioRepository;
import com.branches.relatorio.repository.AssinaturaDeRelatorioRepository;
import com.branches.relatorio.repository.RelatorioRepository;
import com.branches.relatorio.repository.projections.RelatorioDetailsProjection;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.repository.UserTenantRepository;
import com.branches.utils.ItemRelatorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class GenerateRelatorioFileToUsersService {
    private final RelatorioRepository relatorioRepository;
    private final UserTenantRepository userTenantRepository;
    private final OcorrenciaDeRelatorioRepository ocorrenciaDeRelatorioRepository;
    private final AtividadeDeRelatorioRepository atividadeDeRelatorioRepository;
    private final EquipamentoDeRelatorioRepository equipamentoDeRelatorioRepository;
    private final MaoDeObraDeRelatorioRepository maoDeObraDeRelatorioRepository;
    private final ComentarioDeRelatorioRepository comentarioDeRelatorioRepository;
    private final MaterialDeRelatorioRepository materialDeRelatorioRepository;
    private final ArquivoRepository arquivoRepository;
    private final AssinaturaDeRelatorioRepository assinaturaDeRelatorioRepository;
    private final ArquivoDeRelatorioDeUsuarioRepository arquivoDeRelatorioDeUsuarioRepository;
    private final ProcessRelatorioFileToUsersService processRelatorioFileToUsersService;

    public void execute(Long relatorioId) {
        RelatorioDetailsProjection details = getRelatorioDetailsOrThrow(relatorioId);

        List<UserTenantEntity> userTenants = getUserTenantsWithAccessToRelatorio(details.getTenantId(), details.getObraId());

        processFilesToUsers(details, userTenants);
    }

    public void executeOnlyToNecessaryUsers(Long relatorioId, ItemRelatorio itemAtualizado) {
        RelatorioDetailsProjection details = getRelatorioDetailsOrThrow(relatorioId);

        List<UserTenantEntity> userTenants = getUserTenantsWithAccessToRelatorio(details.getTenantId(), details.getObraId());

        List<UserTenantEntity> necessaryUsers = userTenants.stream()
                .filter(userTenant -> {
                    switch (itemAtualizado) {
                        case OCORRENCIAS -> {
                            return userTenant.getAuthorities().getItensDeRelatorio().getOcorrencias();
                        }
                        case ATIVIDADES -> {
                            return userTenant.getAuthorities().getItensDeRelatorio().getAtividades();
                        }
                        case EQUIPAMENTOS -> {
                            return userTenant.getAuthorities().getItensDeRelatorio().getEquipamentos();
                        }
                        case MAO_DE_OBRA -> {
                            return userTenant.getAuthorities().getItensDeRelatorio().getMaoDeObra();
                        }
                        case COMENTARIOS -> {
                            return userTenant.getAuthorities().getItensDeRelatorio().getComentarios();
                        }
                        case MATERIAIS -> {
                            return userTenant.getAuthorities().getItensDeRelatorio().getMateriais();
                        }
                        case FOTOS -> {
                            return userTenant.getAuthorities().getItensDeRelatorio().getFotos();
                        }
                        case VIDEOS -> {
                            return userTenant.getAuthorities().getItensDeRelatorio().getVideos();
                        }
                        case CONDICAO_CLIMATICA -> {
                            return userTenant.getAuthorities().getItensDeRelatorio().getCondicaoDoClima();
                        }
                        case HORARIO_DE_TRABALHO -> {
                            return userTenant.getAuthorities().getItensDeRelatorio().getHorarioDeTrabalho();
                        }
                        default -> {
                            return false;
                        }
                    }
                }).toList();

        processFilesToUsers(details, necessaryUsers);
    }

    private RelatorioDetailsProjection getRelatorioDetailsOrThrow(Long relatorioId) {
        return relatorioRepository.findDetailsWithoutPdfLinkById(relatorioId)
                .orElseThrow(() -> new NotFoundException("Relatório não encontrado com o id: " + relatorioId));
    }

    private List<UserTenantEntity> getUserTenantsWithAccessToRelatorio(Long tenantId, Long obraId) {
        return userTenantRepository.findAllByTenantIdAndUserHasAccessToObraId(
                tenantId,
                obraId
        );
    }

    private void processFilesToUsers(
            RelatorioDetailsProjection details,
            List<UserTenantEntity> userTenants
    ) {
        Map<Long, ArquivoDeRelatorioDeUsuarioEntity> mapUserIdAndArquivoDeRelatorio = getMapOfExistingArquivoDeRelatorioToUserId(userTenants, details.getId());
        List<OcorrenciaDeRelatorioEntity> ocorrencias = fetchOcorrenciasIfAllowed(details);
        List<AtividadeDeRelatorioEntity> atividades = fetchAtividadesIfAllowed(details);
        List<EquipamentoDeRelatorioEntity> equipamentos = fetchEquipamentosIfAllowed(details);
        List<MaoDeObraDeRelatorioEntity> maoDeObra = fetchMaoDeObraIfAllowed(details);
        List<ComentarioDeRelatorioEntity> comentarios = fetchComentariosIfAllowed(details);
        List<MaterialDeRelatorioEntity> materiais = fetchMateriaisIfAllowed(details);
        List<ArquivoEntity> arquivos = fetchArquivoIfAllowsFotosOuVideos(details);
        List<AssinaturaDeRelatorioEntity> assinaturas = assinaturaDeRelatorioRepository.findAllByRelatorioId(details.getId());

        List<ArquivoDeRelatorioDeUsuarioEntity> arquivosToSave = processRelatorioFileToUsersService.execute(
                details,
                userTenants,
                mapUserIdAndArquivoDeRelatorio,
                ocorrencias,
                atividades,
                equipamentos,
                maoDeObra,
                comentarios,
                materiais,
                assinaturas,
                arquivos
        );

        arquivoDeRelatorioDeUsuarioRepository.saveAll(arquivosToSave);
    }

    private List<OcorrenciaDeRelatorioEntity> fetchOcorrenciasIfAllowed(RelatorioDetailsProjection details) {
        if (details.getShowOcorrencias()) {
            return ocorrenciaDeRelatorioRepository.findAllByRelatorioId(details.getId());
        }
        return Collections.emptyList();
    }

    private List<AtividadeDeRelatorioEntity> fetchAtividadesIfAllowed(RelatorioDetailsProjection details) {
        if (details.getShowAtividades()) {
            return atividadeDeRelatorioRepository.findAllByRelatorioId(details.getId());
        }
        return Collections.emptyList();
    }

    private List<EquipamentoDeRelatorioEntity> fetchEquipamentosIfAllowed(RelatorioDetailsProjection details) {
        if (details.getShowEquipamentos()) {
            return equipamentoDeRelatorioRepository.findAllByRelatorioId(details.getId());
        }
        return Collections.emptyList();
    }

    private List<MaoDeObraDeRelatorioEntity> fetchMaoDeObraIfAllowed(RelatorioDetailsProjection details) {
        if (details.getShowMaoDeObra()) {
            return maoDeObraDeRelatorioRepository.findAllByRelatorioId(details.getId());
        }
        return Collections.emptyList();
    }

    private List<ComentarioDeRelatorioEntity> fetchComentariosIfAllowed(RelatorioDetailsProjection details) {
        if (details.getShowComentarios()) {
            return comentarioDeRelatorioRepository.findAllByRelatorioId(details.getId());
        }
        return Collections.emptyList();
    }

    private List<MaterialDeRelatorioEntity> fetchMateriaisIfAllowed(RelatorioDetailsProjection details) {
        if (details.getShowMateriais()) {
            return materialDeRelatorioRepository.findAllByRelatorioId(details.getId());
        }
        return Collections.emptyList();
    }

    private List<ArquivoEntity> fetchArquivoIfAllowsFotosOuVideos(RelatorioDetailsProjection details) {
        if (details.getShowFotos() || details.getShowVideos()) {
            return arquivoRepository.findAllByRelatorioId(details.getId());
        }
        return Collections.emptyList();
    }

    private Map<Long, ArquivoDeRelatorioDeUsuarioEntity> getMapOfExistingArquivoDeRelatorioToUserId(List<UserTenantEntity> userTenants, Long relatorioId) {
        List<ArquivoDeRelatorioDeUsuarioEntity> existenteArquivoDeRelatorioDosUsers = arquivoDeRelatorioDeUsuarioRepository.findAllByRelatorioIdAndUserIdIn(
                relatorioId,
                userTenants.stream().map(ut -> ut.getUser().getId()).toList());

        return existenteArquivoDeRelatorioDosUsers.stream()
                .collect(Collectors.toMap(ArquivoDeRelatorioDeUsuarioEntity::getUserId, arq -> arq));
    }
}
