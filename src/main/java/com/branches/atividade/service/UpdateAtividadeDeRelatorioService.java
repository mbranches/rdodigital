package com.branches.atividade.service;

import com.branches.atividade.domain.AtividadeDeRelatorioCampoPersonalizadoEntity;
import com.branches.maodeobra.domain.MaoDeObraEntity;
import com.branches.maodeobra.service.GetMaoDeObraListByIdInAndTenantIdAndTypeService;
import com.branches.obra.service.CheckIfUserHasAccessToObraService;
import com.branches.obra.domain.enums.TipoMaoDeObra;
import com.branches.relatorio.dto.request.CampoPersonalizadoRequest;
import com.branches.relatorio.service.CheckIfUserHasAccessToEditRelatorioService;
import com.branches.relatorio.service.GetRelatorioByIdExternoAndTenantIdService;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import com.branches.utils.CalculateMinutosTotais;
import com.branches.atividade.domain.AtividadeDeRelatorioEntity;
import com.branches.maodeobra.domain.MaoDeObraDeAtividadeDeRelatorioEntity;
import com.branches.relatorio.domain.RelatorioEntity;
import com.branches.atividade.dto.request.UpdateAtividadeDeRelatorioRequest;
import com.branches.atividade.repository.AtividadeDeRelatorioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UpdateAtividadeDeRelatorioService {

    private final AtividadeDeRelatorioRepository atividadeDeRelatorioRepository;
    private final CalculateMinutosTotais calculateMinutosTotais;
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final CheckIfUserHasAccessToEditRelatorioService checkIfUserHasAccessToEditRelatorioService;
    private final GetRelatorioByIdExternoAndTenantIdService getRelatorioByIdExternoAndTenantIdService;
    private final GetAtividadeDeRelatorioByIdAndRelatorioIdService getAtividadeDeRelatorioByIdAndRelatorioIdService;
    private final CheckIfConfiguracaoDeRelatorioDaObraPermiteAtividade checkIfConfiguracaoDeRelatorioDaObraPermiteAtividade;
    private final CheckIfUserCanViewAtividadesService checkIfUserCanViewAtividadesService;
    private final GetMaoDeObraListByIdInAndTenantIdAndTypeService getMaoDeObraListByIdInAndTenantIdAndTypeService;
    private final CheckIfUserHasAccessToObraService checkIfUserHasAccessToObraService;

    @Transactional
    public void execute(UpdateAtividadeDeRelatorioRequest request, Long id, String relatorioExternalId, String tenantExternalId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity userTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        RelatorioEntity relatorio = getRelatorioByIdExternoAndTenantIdService.execute(relatorioExternalId, tenantId);

        checkIfUserHasAccessToObraService.execute(userTenant, relatorio.getObraId());
        checkIfUserHasAccessToEditRelatorioService.execute(userTenant, relatorio.getStatus());
        checkIfConfiguracaoDeRelatorioDaObraPermiteAtividade.execute(relatorio, tenantId);
        checkIfUserCanViewAtividadesService.execute(userTenant);

        AtividadeDeRelatorioEntity entity = getAtividadeDeRelatorioByIdAndRelatorioIdService.execute(id, relatorio.getId());
        List<AtividadeDeRelatorioCampoPersonalizadoEntity> camposPersonalizadosToSave = getCamposPersonalizadosToSave(request.camposPersonalizados(), tenantId, entity);

        entity.setDescricao(request.descricao());
        entity.setQuantidadeRealizada(request.quantidadeRealizada());
        entity.setUnidadeMedida(request.unidadeMedida());
        entity.setPorcentagemConcluida(request.porcentagemConcluida());
        entity.setStatus(request.status());
        entity.setHoraInicio(request.horaInicio());
        entity.setHoraFim(request.horaFim());
        entity.setMinutosTotais(calculateMinutosTotais.execute(request.horaInicio(), request.horaFim(), null));

        entity.getCamposPersonalizados().clear();
        entity.getCamposPersonalizados().addAll(camposPersonalizadosToSave);

        List<MaoDeObraDeAtividadeDeRelatorioEntity> maoDeObraToSave = createMaoDeObraDeRelatorioListToSave(entity, request.maoDeObraIds(), tenantId, relatorio.getTipoMaoDeObra());

        entity.getMaoDeObra().clear();
        entity.getMaoDeObra().addAll(maoDeObraToSave);

        atividadeDeRelatorioRepository.save(entity);
    }

    private List<AtividadeDeRelatorioCampoPersonalizadoEntity> getCamposPersonalizadosToSave(List<CampoPersonalizadoRequest> requestList, Long tenantId, AtividadeDeRelatorioEntity atividadeDeRelatorioEntity) {
        if (requestList == null || requestList.isEmpty()) {
            return List.of();
        }

        return requestList.stream()
                .map(request -> request.toEntity(tenantId))
                .map(campoPersonalizadoEntity ->
                        AtividadeDeRelatorioCampoPersonalizadoEntity.from(atividadeDeRelatorioEntity, campoPersonalizadoEntity, tenantId)
                ).toList();
    }

    private List<MaoDeObraDeAtividadeDeRelatorioEntity> createMaoDeObraDeRelatorioListToSave(AtividadeDeRelatorioEntity entity, List<Long> maoDeObraIds, Long tenantId, TipoMaoDeObra tipoMaoDeObra) {
        if (maoDeObraIds == null || maoDeObraIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<MaoDeObraEntity> maoDeObraEntities = getMaoDeObraListByIdInAndTenantIdAndTypeService.execute(new HashSet<>(maoDeObraIds), tenantId, tipoMaoDeObra);

        return maoDeObraEntities.stream()
                .map(mo -> MaoDeObraDeAtividadeDeRelatorioEntity.builder()
                        .atividadeDeRelatorio(entity)
                        .maoDeObra(mo)
                        .funcao(mo.getFuncao())
                        .tenantId(mo.getTenantId())
                        .build())
                .collect(Collectors.toList());
    }
}
