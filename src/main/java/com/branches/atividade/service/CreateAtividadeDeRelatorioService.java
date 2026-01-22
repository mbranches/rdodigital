package com.branches.atividade.service;

import com.branches.atividade.domain.AtividadeDeRelatorioCampoPersonalizadoEntity;
import com.branches.atividade.domain.AtividadeDeRelatorioEntity;
import com.branches.atividade.dto.request.CreateAtividadeDeRelatorioRequest;
import com.branches.atividade.dto.response.CreateAtividadeDeRelatorioResponse;
import com.branches.atividade.repository.AtividadeDeRelatorioRepository;
import com.branches.maodeobra.domain.MaoDeObraDeAtividadeDeRelatorioEntity;
import com.branches.maodeobra.domain.MaoDeObraEntity;
import com.branches.maodeobra.repository.MaoDeObraDeAtividadeDeRelatorioRepository;
import com.branches.maodeobra.service.GetMaoDeObraListByIdInAndTenantIdAndTypeService;
import com.branches.obra.controller.CheckIfUserHasAccessToObraService;
import com.branches.obra.domain.enums.TipoMaoDeObra;
import com.branches.relatorio.domain.RelatorioEntity;
import com.branches.relatorio.dto.request.CampoPersonalizadoRequest;
import com.branches.relatorio.service.CheckIfUserHasAccessToEditRelatorioService;
import com.branches.relatorio.service.GetRelatorioByIdExternoAndTenantIdService;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import com.branches.utils.CalculateHorasTotais;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class CreateAtividadeDeRelatorioService {
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final CheckIfUserHasAccessToEditRelatorioService checkIfUserHasAccessToEditRelatorioService;
    private final GetRelatorioByIdExternoAndTenantIdService getRelatorioByIdExternoAndTenantIdService;
    private final CheckIfConfiguracaoDeRelatorioDaObraPermiteAtividade checkIfConfiguracaoDeRelatorioDaObraPermiteAtividade;
    private final CheckIfUserCanViewAtividadesService checkIfUserCanViewAtividadesService;
    private final GetMaoDeObraListByIdInAndTenantIdAndTypeService getMaoDeObraListByIdInAndTenantIdAndTypeService;
    private final CalculateHorasTotais calculateHorasTotais;
    private final AtividadeDeRelatorioRepository atividadeDeRelatorioRepository;
    private final MaoDeObraDeAtividadeDeRelatorioRepository maoDeObraDeAtividadeDeRelatorioRepository;
    private final CheckIfUserHasAccessToObraService checkIfUserHasAccessToObraService;

    public CreateAtividadeDeRelatorioResponse execute(CreateAtividadeDeRelatorioRequest request, String relatorioExternalId, String tenantExternalId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        RelatorioEntity relatorio = getRelatorioByIdExternoAndTenantIdService.execute(relatorioExternalId, tenantId);

        checkIfUserHasAccessToObraService.execute(currentUserTenant, relatorio.getObraId());
        checkIfUserHasAccessToEditRelatorioService.execute(currentUserTenant, relatorio.getStatus());
        checkIfConfiguracaoDeRelatorioDaObraPermiteAtividade.execute(relatorio, tenantId);
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
                .totalHoras(calculateHorasTotais.execute(request.horaInicio(), request.horaFim(), null))
                .status(request.status())
                .tenantId(tenantId)
                .build();

        List<AtividadeDeRelatorioCampoPersonalizadoEntity> camposPersonalizados = getCamposPersonalizadosToSave(request.camposPersonalizados(), atividadeDeRelatorio, tenantId);
        atividadeDeRelatorio.setCamposPersonalizados(camposPersonalizados);

        AtividadeDeRelatorioEntity saved = atividadeDeRelatorioRepository.save(atividadeDeRelatorio);

        List<MaoDeObraDeAtividadeDeRelatorioEntity> maoDeObra = saveMaoDeObraDeAtividade(saved, maoDeObraEntities, tenantId);
        saved.setMaoDeObra(maoDeObra);

        return CreateAtividadeDeRelatorioResponse.from(saved);
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
