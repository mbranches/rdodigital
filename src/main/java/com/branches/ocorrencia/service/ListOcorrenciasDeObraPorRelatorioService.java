package com.branches.ocorrencia.service;

import com.branches.obra.service.CheckIfUserHasAccessToObraService;
import com.branches.obra.domain.ObraEntity;
import com.branches.obra.service.GetObraByIdExternoAndTenantIdService;
import com.branches.ocorrencia.domain.OcorrenciaDeRelatorioEntity;
import com.branches.ocorrencia.dto.response.OcorrenciaDeRelatorioResponse;
import com.branches.ocorrencia.repository.OcorrenciaDeRelatorioRepository;
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
public class ListOcorrenciasDeObraPorRelatorioService {
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final GetObraByIdExternoAndTenantIdService getObraByIdExternoAndTenantIdService;
    private final CheckIfUserHasAccessToObraService checkIfUserHasAccessToObraService;
    private final OcorrenciaDeRelatorioRepository ocorrenciaDeRelatorioRepository;
    private final CheckIfUserCanViewOcorrenciasService checkIfUserCanViewOcorrenciasService;
    private final CheckIfConfiguracaoDeRelatorioDaObraPermiteOcorrenciaService checkIfConfiguracaoDeRelatorioDaObraPermiteOcorrenciaService;

    public PageResponse<ItemPorRelatorioResponse<OcorrenciaDeRelatorioResponse>> execute(String tenantExternalId, String obraExternalId, List<UserTenantEntity> userTenants, PageableRequest pageableRequest) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        ObraEntity obra = getObraByIdExternoAndTenantIdService.execute(obraExternalId, tenantId);

        checkIfUserHasAccessToObraService.execute(currentUserTenant, obra.getId());
        checkIfUserCanViewOcorrenciasService.execute(currentUserTenant);
        checkIfConfiguracaoDeRelatorioDaObraPermiteOcorrenciaService.execute(obra);

        PageRequest pageRequest = pageableRequest.toPageRequest("enversCreatedDate");
        Boolean canViewOnlyAprovados = currentUserTenant.getAuthorities().getRelatorios().getCanViewOnlyAprovados();

        Page<OcorrenciaDeRelatorioEntity> ocorrencias = ocorrenciaDeRelatorioRepository.findAllByObraIdAndTenantId(obra.getId(), tenantId, canViewOnlyAprovados, pageRequest);

        Map<RelatorioEntity, List<OcorrenciaDeRelatorioEntity>> MapRelatorioAndOcorrencias = ocorrencias.stream()
                .collect(Collectors.groupingBy(OcorrenciaDeRelatorioEntity::getRelatorio));

        List<ItemPorRelatorioResponse<OcorrenciaDeRelatorioResponse>> itensPorRelatorio = MapRelatorioAndOcorrencias.entrySet().stream()
                .map(entry -> {
                    RelatorioEntity relatorio = entry.getKey();
                    List<OcorrenciaDeRelatorioEntity> ocorrenciasDoRelatorio = entry.getValue();
                    List<OcorrenciaDeRelatorioResponse> ocorrenciasResponse = ocorrenciasDoRelatorio.stream()
                            .map(OcorrenciaDeRelatorioResponse::from)
                            .toList();
                    return ItemPorRelatorioResponse.from(relatorio, ocorrenciasResponse);
                })
                .toList();

        return new PageResponse<>(
                ocorrencias.getSize(),
                ocorrencias.getNumber(),
                ocorrencias.getTotalElements(),
                itensPorRelatorio,
                ocorrencias.isFirst(),
                ocorrencias.isLast()
        );
    }
}
