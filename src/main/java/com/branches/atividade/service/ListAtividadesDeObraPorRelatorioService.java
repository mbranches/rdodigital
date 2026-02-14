package com.branches.atividade.service;

import com.branches.atividade.domain.AtividadeDeRelatorioEntity;
import com.branches.atividade.dto.response.AtividadeDeRelatorioResponse;
import com.branches.atividade.repository.AtividadeDeRelatorioRepository;
import com.branches.obra.service.CheckIfUserHasAccessToObraService;
import com.branches.obra.domain.ObraEntity;
import com.branches.obra.service.GetObraByIdExternoAndTenantIdService;
import com.branches.relatorio.domain.RelatorioEntity;
import com.branches.relatorio.dto.response.ItemPorRelatorioResponse;
import com.branches.shared.pagination.PageResponse;
import com.branches.shared.pagination.PageableRequest;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ListAtividadesDeObraPorRelatorioService {
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final GetObraByIdExternoAndTenantIdService getObraByIdExternoAndTenantIdService;
    private final CheckIfUserHasAccessToObraService checkIfUserHasAccessToObraService;
    private final AtividadeDeRelatorioRepository atividadeDeRelatorioRepository;

    public PageResponse<ItemPorRelatorioResponse<AtividadeDeRelatorioResponse>> execute(String tenantExternalId, String obraExternalId, List<UserTenantEntity> userTenants, PageableRequest pageableRequest) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        ObraEntity obra = getObraByIdExternoAndTenantIdService.execute(obraExternalId, tenantId);

        checkIfUserHasAccessToObraService.execute(currentUserTenant, obra.getId());

        PageRequest pageRequest = pageableRequest.toPageRequest("enversCreatedDate");
        Boolean canViewOnlyAprovados = currentUserTenant.getAuthorities().getRelatorios().getCanViewOnlyAprovados();

        Page<AtividadeDeRelatorioEntity> atividades = atividadeDeRelatorioRepository.findAllByObraIdAndTenantId(obra.getId(), tenantId, canViewOnlyAprovados, pageRequest);

        Map<RelatorioEntity, List<AtividadeDeRelatorioEntity>> MapRelatorioAndAtividades = atividades.stream()
                .collect(Collectors.groupingBy(AtividadeDeRelatorioEntity::getRelatorio));

        List<ItemPorRelatorioResponse<AtividadeDeRelatorioResponse>> itensPorRelatorio = MapRelatorioAndAtividades.entrySet().stream()
                .map(entry -> {
                    RelatorioEntity relatorio = entry.getKey();
                    List<AtividadeDeRelatorioEntity> atividadesDoRelatorio = entry.getValue();
                    List<AtividadeDeRelatorioResponse> atividadesResponse = atividadesDoRelatorio.stream()
                            .map(AtividadeDeRelatorioResponse::from)
                            .toList();
                    return ItemPorRelatorioResponse.from(relatorio, atividadesResponse);
                })
                .toList();

        return new PageResponse<>(
                atividades.getSize(),
                atividades.getNumber(),
                atividades.getTotalElements(),
                itensPorRelatorio,
                atividades.isFirst(),
                atividades.isLast()
        );
    }
}
