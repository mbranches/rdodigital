package com.branches.obra.service;

import com.branches.arquivo.domain.ArquivoEntity;
import com.branches.arquivo.repository.ArquivoRepository;
import com.branches.exception.NotFoundException;
import com.branches.obra.domain.enums.StatusObra;
import com.branches.obra.dto.response.GetObraDetailsByIdExternoResponse;
import com.branches.obra.repository.ObraRepository;
import com.branches.obra.repository.projections.ObraDetailsProjection;
import com.branches.relatorio.repository.RelatorioRepository;
import com.branches.relatorio.repository.projections.RelatorioProjection;
import com.branches.shared.calculators.CalculatePrazoDecorrido;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RequiredArgsConstructor
@Service
public class GetObraDetailsByIdExternoService {
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final RelatorioRepository relatorioRepository;
    private final ObraRepository obraRepository;
    private final CheckIfUserHasAccessToObraService checkIfUserHasAccessToObraService;
    private final ArquivoRepository arquivoRepository;
    private final CalculatePrazoDecorrido calculatePrazoDecorrido;

    public GetObraDetailsByIdExternoResponse execute(String idExterno, String tenantExternalId, List<UserTenantEntity> userTenants) {
        Long tenantDaObraId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantDaObraId);

        ObraDetailsProjection obra = obraRepository.findObraDetailsByIdExternoAndTenantId(idExterno, tenantDaObraId)
                .orElseThrow(() -> new NotFoundException("Obra não encontrada com o id: " + idExterno));

        List<RelatorioProjection> relatoriosRecentes = relatorioRepository.findTop5ByObraIdProjection(obra.getId());

        List<ArquivoEntity> fotosRecentes = arquivoRepository.findTop5FotosDeRelatoriosByObraId(obra.getId());

        checkIfUserHasAccessToObraService.execute(currentUserTenant, obra.getId());
        long prazoContratual = ChronoUnit.DAYS.between(obra.getDataInicio(), obra.getDataPrevistaFim());

        long diferencaEntreHojeEDataFim = ChronoUnit.DAYS.between(LocalDate.now(), obra.getDataPrevistaFim());

        Long prazoPraVencer = obra.getStatus().equals(StatusObra.CONCLUIDA) ? 0L : Math.max(diferencaEntreHojeEDataFim, 0L);

        LocalDate dataPraCompararDiasDecorrido = obra.getDataFimReal() != null ? obra.getDataFimReal() : LocalDate.now();

        long diasDecorridos = ChronoUnit.DAYS.between(obra.getDataInicio(), dataPraCompararDiasDecorrido);
        long prazoDecorrido = diasDecorridos < 0 ? 0 : Math.min(diasDecorridos, prazoContratual);
        BigDecimal porcentagemPrazoDecorrido = calculatePrazoDecorrido.execute(obra.getDataInicio(), obra.getDataPrevistaFim(), dataPraCompararDiasDecorrido);

        return GetObraDetailsByIdExternoResponse.from(obra, relatoriosRecentes, fotosRecentes, prazoContratual, prazoPraVencer, porcentagemPrazoDecorrido, prazoDecorrido);
    }
}
