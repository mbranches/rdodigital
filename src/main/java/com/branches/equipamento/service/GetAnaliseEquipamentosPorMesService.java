package com.branches.equipamento.service;

import com.branches.equipamento.dto.response.AnaliseDeEquipamentosPorMesResponse;
import com.branches.equipamento.dto.response.QuantidadeEquipamentoPorMesResponse;
import com.branches.equipamento.repository.EquipamentoRepository;
import com.branches.equipamento.repository.projections.QuantidadeEquipamentoPorMesProjection;
import com.branches.equipamento.repository.projections.TotalDeEquipamentoPorMesProjection;
import com.branches.obra.service.CheckIfUserHasAccessToObraService;
import com.branches.obra.domain.ObraEntity;
import com.branches.obra.service.GetObraByIdExternoAndTenantIdService;
import com.branches.shared.dto.response.TotalPorMesResponse;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class GetAnaliseEquipamentosPorMesService {

    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final GetObraByIdExternoAndTenantIdService getObraByIdExternoAndTenantIdService;
    private final CheckIfConfiguracaoDeRelatorioDaObraPermiteEquipamentoService checkIfConfiguracaoDeRelatorioDaObraPermiteEquipamentoService;
    private final CheckIfUserHasAccessToObraService checkIfUserHasAccessToObraService;
    private final CheckIfUserCanViewEquipamentosService checkIfUserCanViewEquipamentosService;
    private final EquipamentoRepository equipamentoRepository;

    public AnaliseDeEquipamentosPorMesResponse execute(String tenantExternalId, Integer year, String obraExternalId, Long equipamentoId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        if (obraExternalId != null) {
            ObraEntity obra = getObraByIdExternoAndTenantIdService.execute(obraExternalId, tenantId);
            checkIfUserHasAccessToObraService.execute(currentUserTenant, obra.getId());
            checkIfConfiguracaoDeRelatorioDaObraPermiteEquipamentoService.execute(obra);
        }

        checkIfUserCanViewEquipamentosService.execute(currentUserTenant);

        List<TotalDeEquipamentoPorMesProjection> totalDeEquipamentoPorMesProjectionList = equipamentoRepository.findTotalEquipamentoPorMes(tenantId, year, obraExternalId, equipamentoId);
        List<QuantidadeEquipamentoPorMesProjection> quantidadeEquipamentoPorMesProjectionList = equipamentoRepository.findQuantidadeEquipamentoPorMes(tenantId, year, obraExternalId);

        Map<Integer, Long> mapMesAndQuantidade = totalDeEquipamentoPorMesProjectionList.stream()
                .collect(Collectors.toMap(
                        TotalDeEquipamentoPorMesProjection::getMes,
                        TotalDeEquipamentoPorMesProjection::getQuantidade
                ));

        Map<Long, List<QuantidadeEquipamentoPorMesProjection>> mapEquipamentoIdToQuantidadePorMes = quantidadeEquipamentoPorMesProjectionList.stream()
                .collect(Collectors.groupingBy(QuantidadeEquipamentoPorMesProjection::getEquipamentoId));

        List<TotalPorMesResponse> totalDeEquipamentosPorMes = Arrays.stream(Month.values())
                .map(Month::getValue)
                .map(mesInt -> {
                    Long quantidade = mapMesAndQuantidade.getOrDefault(mesInt, 0L);

                    return new TotalPorMesResponse(mesInt, quantidade);
                })
                .toList();


        List<QuantidadeEquipamentoPorMesResponse> quantidadeEquipamentoPorMesResponses = mapEquipamentoIdToQuantidadePorMes.values().stream()
                .map(QuantidadeEquipamentoPorMesResponse::from)
                .toList();

        return new AnaliseDeEquipamentosPorMesResponse(totalDeEquipamentosPorMes, quantidadeEquipamentoPorMesResponses);
    }
}
