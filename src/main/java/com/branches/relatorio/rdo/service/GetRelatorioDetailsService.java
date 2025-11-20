package com.branches.relatorio.rdo.service;

import com.branches.exception.ForbiddenException;
import com.branches.exception.NotFoundException;
import com.branches.relatorio.rdo.domain.*;
import com.branches.relatorio.rdo.domain.enums.StatusRelatorio;
import com.branches.relatorio.rdo.dto.response.GetRelatorioDetailsResponse;
import com.branches.relatorio.rdo.repository.*;
import com.branches.relatorio.rdo.repository.projections.RelatorioDetailsProjection;
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

    public GetRelatorioDetailsResponse execute(String tenantExternalId,
                                               String relatorioExternalId,
                                               List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        RelatorioDetailsProjection relatorioDetails = relatorioRepository.findDetailsByIdExternoAndTenantId(relatorioExternalId, tenantId)
                .orElseThrow(() -> new NotFoundException("Relatório não encontrado com o id: " + relatorioExternalId));

        checkIfUserCanViewRelatorio(currentUserTenant, relatorioDetails.getStatus());

        PermissionsItensDeRelatorio permissionsOfItensRelatorio = currentUserTenant.getAuthorities().getItensDeRelatorio();
        boolean canViewCondicaoDoClima = relatorioDetails.getShowCondicaoClimatica() && permissionsOfItensRelatorio.getCondicaoDoClima();
        boolean canViewOcorrencias = relatorioDetails.getShowOcorrencias() && permissionsOfItensRelatorio.getOcorrencias();
        boolean canVAtividades = relatorioDetails.getShowAtividades() && permissionsOfItensRelatorio.getAtividades();
        boolean canViewEquipamentos = relatorioDetails.getShowEquipamentos() && permissionsOfItensRelatorio.getEquipamentos();
        boolean canViewMaoDeObra = relatorioDetails.getShowMaoDeObra() && permissionsOfItensRelatorio.getMaoDeObra();
        boolean canViewComentarios = relatorioDetails.getShowComentarios() && permissionsOfItensRelatorio.getComentarios();
        boolean canViewMateriais = relatorioDetails.getShowMateriais() && permissionsOfItensRelatorio.getMateriais();
//        TODO: ADICIONAR FOTOS Boolean viewFotos = currentUserTenant.getAuthorities().getItensDeRelatorio().getFotos();

        Long relatorioId = relatorioDetails.getId();

        List<OcorrenciaDeRelatorioEntity> ocorrencias = canViewOcorrencias ? ocorrenciaDeRelatorioRepository.findAllByRelatorioId(relatorioId) : null;
        List<AtividadeDeRelatorioEntity> atividades = canVAtividades ? atividadeDeRelatorioRepository.findAllByRelatorioId(relatorioId) : null;
        List<EquipamentoDeRelatorioEntity> equipamentos = canViewEquipamentos ? equipamentoDeRelatorioRepository.findAllByRelatorioId(relatorioId) : null;
        List<MaoDeObraDeRelatorioEntity> maoDeObra = canViewMaoDeObra ? maoDeObraDeRelatorioRepository.findAllByRelatorioId(relatorioId) : null;
        List<ComentarioDeRelatorioEntity> comentarios = canViewComentarios ? comentarioDeRelatorioRepository.findAllByRelatorioId(relatorioId) : null;
        List<MaterialDeRelatorioEntity> materiais = canViewMateriais ? materialDeRelatorioRepository.findAllByRelatorioId(relatorioId) : null;

        return GetRelatorioDetailsResponse.from(
                relatorioDetails,
                ocorrencias,
                atividades,
                equipamentos,
                maoDeObra,
                comentarios,
                materiais,
                canViewCondicaoDoClima
        );
    }

    private void checkIfUserCanViewRelatorio(UserTenantEntity userTenant, StatusRelatorio statusRelatorio) {
        if (userTenant.getAuthorities().getRelatorios().getCanViewOnlyAprovados() && statusRelatorio != StatusRelatorio.APROVADO) {
            throw new ForbiddenException();
        }
    }
}
