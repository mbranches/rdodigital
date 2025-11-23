package com.branches.atividade.service;

import com.branches.maodeobra.domain.MaoDeObraEntity;
import com.branches.maodeobra.service.GetMaoDeObraListByIdInAndTenantIdAndTypeService;
import com.branches.relatorio.dto.request.CampoPersonalizadoRequest;
import com.branches.relatorio.service.CheckIfUserHasAccessToEditRelatorioService;
import com.branches.maodeobra.service.GetMaoDeObraDeAtividadeListByAtividadeIdAndIdInService;
import com.branches.relatorio.service.GetRelatorioByIdExternoAndTenantIdService;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import com.branches.utils.CalculateHorasTotais;
import com.branches.atividade.domain.AtividadeDeRelatorioEntity;
import com.branches.maodeobra.domain.MaoDeObraDeAtividadeDeRelatorioEntity;
import com.branches.relatorio.domain.RelatorioEntity;
import com.branches.atividade.dto.request.UpdateAtividadeDeRelatorioRequest;
import com.branches.maodeobra.dto.request.UpdateMaoDeObraDeAtividadeRequest;
import com.branches.atividade.repository.AtividadeDeRelatorioRepository;
import com.branches.maodeobra.repository.MaoDeObraDeAtividadeDeRelatorioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UpdateAtividadeDeRelatorioService {

    private final AtividadeDeRelatorioRepository atividadeDeRelatorioRepository;
    private final MaoDeObraDeAtividadeDeRelatorioRepository maoDeObraDeAtividadeDeRelatorioRepository;
    private final GetMaoDeObraDeAtividadeListByAtividadeIdAndIdInService getMaoDeObraDeAtividadeListByAtividadeIdAndIdInService;
    private final CalculateHorasTotais calculateHorasTotais;
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final CheckIfUserHasAccessToEditRelatorioService checkIfUserHasAccessToEditRelatorioService;
    private final GetRelatorioByIdExternoAndTenantIdService getRelatorioByIdExternoAndTenantIdService;
    private final GetAtividadeDeRelatorioByIdAndRelatorioIdService getAtividadeDeRelatorioByIdAndRelatorioIdService;
    private final CheckIfConfiguracaoDeRelatorioDaObraPermiteAtividade checkIfConfiguracaoDeRelatorioDaObraPermiteAtividade;
    private final CheckIfUserCanViewAtividadesService checkIfUserCanViewAtividadesService;
    private final GetMaoDeObraListByIdInAndTenantIdAndTypeService getMaoDeObraListByIdInAndTenantIdAndTypeService;

    public void execute(UpdateAtividadeDeRelatorioRequest request, Long id, String relatorioExternalId, String tenantExternalId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity userTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        RelatorioEntity relatorio = getRelatorioByIdExternoAndTenantIdService.execute(relatorioExternalId, tenantId);

        checkIfUserHasAccessToEditRelatorioService.execute(userTenant, relatorio.getStatus());

        checkIfConfiguracaoDeRelatorioDaObraPermiteAtividade.execute(relatorio, tenantId);

        checkIfUserCanViewAtividadesService.execute(userTenant);

        AtividadeDeRelatorioEntity entity = getAtividadeDeRelatorioByIdAndRelatorioIdService.execute(id, relatorio.getId());
        List<CampoPersonalizadoRequest> campoPersonalizadoRequest = request.camposPersonalizados() != null
                ? request.camposPersonalizados()
                : List.of();

        entity.setDescricao(request.descricao());
        entity.setQuantidadeRealizada(request.quantidadeRealizada());
        entity.setUnidadeMedida(request.unidadeMedida());
        entity.setPorcentagemConcluida(request.porcentagemConcluida());
        entity.setStatus(request.status());
        entity.setHoraInicio(request.horaInicio());
        entity.setHoraFim(request.horaFim());
        entity.setTotalHoras(calculateHorasTotais.execute(request.horaInicio(), request.horaFim(), null));

        entity.getCamposPersonalizados().clear();
        entity.setCamposPersonalizados(campoPersonalizadoRequest.stream().map(c -> c.toEntity(tenantId)).toList());

        Map<Long, MaoDeObraEntity> maoDeObraEntityMap = getMaoDeObraEntityMap(request.maoDeObra(), tenantId, relatorio);
        entity.getMaoDeObra().clear();
        entity.setMaoDeObra(updateMaoDeObraDeAtividade(request.maoDeObra(), entity, maoDeObraEntityMap));

        atividadeDeRelatorioRepository.save(entity);
    }

    private List<MaoDeObraDeAtividadeDeRelatorioEntity> updateMaoDeObraDeAtividade(List<UpdateMaoDeObraDeAtividadeRequest> requestList, AtividadeDeRelatorioEntity atividade, Map<Long, MaoDeObraEntity> maoDeObraEntityMap) {
        if (requestList == null || requestList.isEmpty()) {
            maoDeObraDeAtividadeDeRelatorioRepository.removeAllByAtividadeDeRelatorioId(atividade.getId());

            return null;
        }

        var existingMaoDeObraList = updateExistingMaoDeObraDeAtividade(requestList, atividade.getId(), maoDeObraEntityMap);

        var newMaoDeObraDeAtividade = createNewMaoDeObraDeAtividade(requestList, atividade, maoDeObraEntityMap);

        List<MaoDeObraDeAtividadeDeRelatorioEntity> maoDeObraDeAtividadeToSave = new ArrayList<>(existingMaoDeObraList);
        maoDeObraDeAtividadeToSave.addAll(newMaoDeObraDeAtividade);

        return maoDeObraDeAtividadeToSave;
    }

    private List<MaoDeObraDeAtividadeDeRelatorioEntity> createNewMaoDeObraDeAtividade(List<UpdateMaoDeObraDeAtividadeRequest> requestList, AtividadeDeRelatorioEntity atividade, Map<Long, MaoDeObraEntity> maoDeObraEntityMap) {
        return requestList.stream()
                .filter(r -> r.id() == null)
                .map(request -> {
                    MaoDeObraDeAtividadeDeRelatorioEntity entity = new MaoDeObraDeAtividadeDeRelatorioEntity();

                    entity.setAtividadeDeRelatorio(atividade);
                    MaoDeObraEntity maoDeObraEntity = maoDeObraEntityMap.get(request.maoDeObraId());
                    entity.setMaoDeObra(maoDeObraEntity);
                    entity.setFuncao(maoDeObraEntity.getFuncao());

                    return entity;
                })
                .toList();
    }

    private List<MaoDeObraDeAtividadeDeRelatorioEntity> updateExistingMaoDeObraDeAtividade(List<UpdateMaoDeObraDeAtividadeRequest> requestList, Long atividadeId, Map<Long, MaoDeObraEntity> maoDeObraEntityMap) {
        Set<Long> ids = requestList.stream()
                .map(UpdateMaoDeObraDeAtividadeRequest::id)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, UpdateMaoDeObraDeAtividadeRequest> requestMap = requestList.stream()
                .filter(r -> r.id() != null)
                .collect(Collectors.toMap(UpdateMaoDeObraDeAtividadeRequest::id, Function.identity()));

        var existingMaoDeObraList = getMaoDeObraDeAtividadeListByAtividadeIdAndIdInService.execute(atividadeId, ids);
        existingMaoDeObraList.forEach(entity -> {
            var request = requestMap.get(entity.getId());

            MaoDeObraEntity maoDeObraEntity = maoDeObraEntityMap.get(request.maoDeObraId());
            entity.setMaoDeObra(maoDeObraEntity);
            entity.setFuncao(maoDeObraEntity.getFuncao());
        });

        return existingMaoDeObraList;
    }

    private Map<Long, MaoDeObraEntity> getMaoDeObraEntityMap(List<UpdateMaoDeObraDeAtividadeRequest> requestList, Long tenantId, RelatorioEntity relatorio) {
        Set<Long> maoDeObraIds = requestList.stream()
                .map(UpdateMaoDeObraDeAtividadeRequest::maoDeObraId)
                .collect(Collectors.toSet());

        List<MaoDeObraEntity> maoDeObraEntities = getMaoDeObraListByIdInAndTenantIdAndTypeService.execute(maoDeObraIds, tenantId, relatorio.getTipoMaoDeObra());

        return maoDeObraEntities.stream()
                .collect(Collectors.toMap(MaoDeObraEntity::getId, Function.identity()));
    }
}
