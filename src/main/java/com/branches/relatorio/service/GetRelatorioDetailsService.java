package com.branches.relatorio.service;

import com.branches.arquivo.domain.ArquivoEntity;
import com.branches.arquivo.repository.ArquivoRepository;
import com.branches.atividade.domain.AtividadeDeRelatorioEntity;
import com.branches.atividade.repository.AtividadeDeRelatorioRepository;
import com.branches.comentarios.model.ComentarioDeRelatorioEntity;
import com.branches.comentarios.repository.ComentarioDeRelatorioRepository;
import com.branches.equipamento.domain.EquipamentoDeRelatorioEntity;
import com.branches.equipamento.repository.EquipamentoDeRelatorioRepository;
import com.branches.exception.ForbiddenException;
import com.branches.exception.NotFoundException;
import com.branches.maodeobra.domain.MaoDeObraDeRelatorioEntity;
import com.branches.maodeobra.repository.MaoDeObraDeRelatorioRepository;
import com.branches.material.domain.MaterialDeRelatorioEntity;
import com.branches.material.repository.MaterialDeRelatorioRepository;
import com.branches.ocorrencia.domain.OcorrenciaDeRelatorioEntity;
import com.branches.ocorrencia.repository.OcorrenciaDeRelatorioRepository;
import com.branches.relatorio.domain.AssinaturaDeRelatorioEntity;
import com.branches.relatorio.domain.enums.StatusRelatorio;
import com.branches.relatorio.dto.response.GetRelatorioDetailsResponse;
import com.branches.relatorio.repository.*;
import com.branches.relatorio.repository.projections.RelatorioDetailsProjection;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.PermissionsItensDeRelatorio;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class GetRelatorioDetailsService {
    private final RelatorioRepository relatorioRepository;
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final OcorrenciaDeRelatorioRepository ocorrenciaDeRelatorioRepository;
    private final AtividadeDeRelatorioRepository atividadeDeRelatorioRepository;
    private final EquipamentoDeRelatorioRepository equipamentoDeRelatorioRepository;
    private final MaoDeObraDeRelatorioRepository maoDeObraDeRelatorioRepository;
    private final ComentarioDeRelatorioRepository comentarioDeRelatorioRepository;
    private final MaterialDeRelatorioRepository materialDeRelatorioRepository;
    private final AssinaturaDeRelatorioRepository assinaturaDeRelatorioRepository;
    private final ArquivoRepository arquivoRepository;

    public GetRelatorioDetailsResponse execute(String tenantExternalId,
                                               String relatorioExternalId,
                                               List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        RelatorioDetailsProjection relatorioDetails = relatorioRepository.findDetailsByIdExternoAndTenantId(relatorioExternalId, tenantId)
                .orElseThrow(() -> new NotFoundException("Relatório não encontrado com o id: " + relatorioExternalId));

        checkIfUserCanViewRelatorio(currentUserTenant, relatorioDetails.getStatus());

        PermissionsItensDeRelatorio permissionsOfItensRelatorio = currentUserTenant.getAuthorities().getItensDeRelatorio();
        boolean canViewHorarioDeTrabalho = relatorioDetails.getShowHorarioDeTrabalho() && permissionsOfItensRelatorio.getHorarioDeTrabalho();
        boolean canViewCondicaoDoClima = relatorioDetails.getShowCondicaoClimatica() && permissionsOfItensRelatorio.getCondicaoDoClima();
        boolean canViewOcorrencias = relatorioDetails.getShowOcorrencias() && permissionsOfItensRelatorio.getOcorrencias();
        boolean canVAtividades = relatorioDetails.getShowAtividades() && permissionsOfItensRelatorio.getAtividades();
        boolean canViewEquipamentos = relatorioDetails.getShowEquipamentos() && permissionsOfItensRelatorio.getEquipamentos();
        boolean canViewMaoDeObra = relatorioDetails.getShowMaoDeObra() && permissionsOfItensRelatorio.getMaoDeObra();
        boolean canViewComentarios = relatorioDetails.getShowComentarios() && permissionsOfItensRelatorio.getComentarios();
        boolean canViewMateriais = relatorioDetails.getShowMateriais() && permissionsOfItensRelatorio.getMateriais();
        boolean canViewFotos = relatorioDetails.getShowFotos() && permissionsOfItensRelatorio.getFotos();
        boolean canViewVideos = relatorioDetails.getShowVideos() && permissionsOfItensRelatorio.getVideos();

        Long relatorioId = relatorioDetails.getId();

        List<OcorrenciaDeRelatorioEntity> ocorrencias = canViewOcorrencias ? ocorrenciaDeRelatorioRepository.findAllByRelatorioId(relatorioId) : null;
        List<AtividadeDeRelatorioEntity> atividades = canVAtividades ? atividadeDeRelatorioRepository.findAllByRelatorioId(relatorioId) : null;
        List<EquipamentoDeRelatorioEntity> equipamentos = canViewEquipamentos ? equipamentoDeRelatorioRepository.findAllByRelatorioId(relatorioId) : null;
        List<MaoDeObraDeRelatorioEntity> maoDeObra = canViewMaoDeObra ? maoDeObraDeRelatorioRepository.findAllByRelatorioId(relatorioId) : null;
        List<ComentarioDeRelatorioEntity> comentarios = canViewComentarios ? comentarioDeRelatorioRepository.findAllByRelatorioId(relatorioId) : null;
        List<MaterialDeRelatorioEntity> materiais = canViewMateriais ? materialDeRelatorioRepository.findAllByRelatorioId(relatorioId) : null;
        List<AssinaturaDeRelatorioEntity> assinaturas = assinaturaDeRelatorioRepository.findAllByRelatorioId(relatorioId);

        List<ArquivoEntity> arquivos = arquivoRepository.findAllByRelatorioId(relatorioId);
        List<ArquivoEntity> fotos = canViewFotos ? arquivos.stream().filter(ArquivoEntity::getIsFoto).toList() : null;
        List<ArquivoEntity> videos = canViewVideos ? arquivos.stream().filter(ArquivoEntity::getIsVideo).toList() : null;

        return GetRelatorioDetailsResponse.from(
                relatorioDetails,
                ocorrencias,
                atividades,
                equipamentos,
                maoDeObra,
                comentarios,
                materiais,
                assinaturas,
                fotos,
                videos,
                canViewCondicaoDoClima,
                canViewHorarioDeTrabalho
        );
    }

    private void checkIfUserCanViewRelatorio(UserTenantEntity userTenant, StatusRelatorio statusRelatorio) {
        if (userTenant.getAuthorities().getRelatorios().getCanViewOnlyAprovados() && statusRelatorio != StatusRelatorio.APROVADO) {
            throw new ForbiddenException();
        }
    }
}
