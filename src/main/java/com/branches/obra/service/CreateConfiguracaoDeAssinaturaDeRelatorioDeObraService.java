package com.branches.obra.service;

import com.branches.exception.BadRequestException;
import com.branches.obra.domain.ConfiguracaoDeAssinaturaDeRelatorioEntity;
import com.branches.obra.domain.ConfiguracaoRelatoriosEntity;
import com.branches.obra.domain.ObraEntity;
import com.branches.obra.dto.request.CreateConfigDeAssinaturaDeRelatorioDeObraRequest;
import com.branches.obra.dto.response.ConfiguracaoDeAssinaturaDeRelatorioResponse;
import com.branches.obra.repository.ConfiguracaoDeAssinaturaDeRelatorioRepository;
import com.branches.relatorio.domain.AssinaturaDeRelatorioEntity;
import com.branches.relatorio.domain.RelatorioEntity;
import com.branches.relatorio.repository.AssinaturaDeRelatorioRepository;
import com.branches.relatorio.repository.RelatorioRepository;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CreateConfiguracaoDeAssinaturaDeRelatorioDeObraService {
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final GetObraByIdExternoAndTenantIdService getObraByIdExternoAndTenantIdService;
    private final CheckIfUserCanEditObraService checkIfUserCanEditObraService;
    private final ConfiguracaoDeAssinaturaDeRelatorioRepository configuracaoDeAssinaturaDeRelatorioRepository;
    private final RelatorioRepository relatorioRepository;
    private final AssinaturaDeRelatorioRepository assinaturaDeRelatorioRepository;

    public ConfiguracaoDeAssinaturaDeRelatorioResponse execute(CreateConfigDeAssinaturaDeRelatorioDeObraRequest request, String obraExternalId, String tenantExternalId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);
        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        ObraEntity obra = getObraByIdExternoAndTenantIdService.execute(obraExternalId, tenantId);

        checkIfUserCanEditObraService.execute(currentUserTenant, obra.getId());

        ConfiguracaoRelatoriosEntity configuracaoRelatorios = obra.getConfiguracaoRelatorios();
        List<ConfiguracaoDeAssinaturaDeRelatorioEntity> configAssinaturas = configuracaoRelatorios.getConfiguracoesDeAssinaturaDeRelatorio();

        if (configAssinaturas.size() >= 6) {
            throw new BadRequestException("Número máximo de assinaturas de relatório atingido (6)");
        }

        var newConfig = ConfiguracaoDeAssinaturaDeRelatorioEntity.builder()
                .nomeAssinante(request.nomeAssinante())
                .configuracaoRelatorios(configuracaoRelatorios)
                .tenantId(tenantId)
                .build();

        ConfiguracaoDeAssinaturaDeRelatorioEntity saved = configuracaoDeAssinaturaDeRelatorioRepository.save(newConfig);

        configAssinaturas.add(saved);

        addNewAssinaturaToExistingRelatorios(obra.getId(), saved);

        return ConfiguracaoDeAssinaturaDeRelatorioResponse.from(saved);
    }

    private void addNewAssinaturaToExistingRelatorios(Long obraId, ConfiguracaoDeAssinaturaDeRelatorioEntity saved) {
        List<RelatorioEntity> relatorios = relatorioRepository.findAllByObraId(obraId);

        List<AssinaturaDeRelatorioEntity> newAssinaturas = relatorios.stream()
                .map(r -> {
                    AssinaturaDeRelatorioEntity assinatura = AssinaturaDeRelatorioEntity.builder()
                                    .relatorio(r)
                                    .configuracao(saved)
                                    .tenantId(r.getTenantId())
                                    .build();

                    return assinatura;
                }).toList();

        assinaturaDeRelatorioRepository.saveAll(newAssinaturas);
    }
}
