package com.branches.atividade.service;

import com.branches.atividade.domain.AtividadeDeRelatorioEntity;
import com.branches.atividade.dto.request.CreateAtividadeDeRelatorioRequest;
import com.branches.atividade.dto.response.CreateAtividadeDeRelatorioResponse;
import com.branches.atividade.repository.AtividadeDeRelatorioRepository;
import com.branches.maodeobra.domain.MaoDeObraDeAtividadeDeRelatorioEntity;
import com.branches.maodeobra.domain.MaoDeObraEntity;
import com.branches.maodeobra.repository.MaoDeObraDeAtividadeDeRelatorioRepository;
import com.branches.maodeobra.service.GetMaoDeObraListByIdInAndTenantIdAndTypeService;
import com.branches.obra.domain.enums.TipoMaoDeObra;
import com.branches.relatorio.domain.CampoPersonalizadoEntity;
import com.branches.relatorio.domain.RelatorioEntity;
import com.branches.relatorio.service.CheckIfUserHasAccessToEditRelatorioService;
import com.branches.relatorio.service.GetRelatorioByIdExternoAndTenantIdService;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import com.branches.utils.CalculateHorasTotais;
import com.branches.utils.ValidateHoraInicioAndHoraFim;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

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
    private final ValidateHoraInicioAndHoraFim validateHoraInicioAndHoraFim;
    private final AtividadeDeRelatorioRepository atividadeDeRelatorioRepository;
    private final MaoDeObraDeAtividadeDeRelatorioRepository maoDeObraDeAtividadeDeRelatorioRepository;

    public CreateAtividadeDeRelatorioResponse execute(CreateAtividadeDeRelatorioRequest request, String relatorioExternalId, String tenantExternalId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        RelatorioEntity relatorio = getRelatorioByIdExternoAndTenantIdService.execute(relatorioExternalId, tenantId);

        checkIfUserHasAccessToEditRelatorioService.execute(currentUserTenant, relatorio.getStatus());

        checkIfConfiguracaoDeRelatorioDaObraPermiteAtividade.execute(relatorio, tenantId);

        checkIfUserCanViewAtividadesService.execute(currentUserTenant);

        List<MaoDeObraEntity> maoDeObraEntities = getMaoDeObraDaAtividade(request, tenantId, relatorio.getTipoMaoDeObra());

        validateHoraInicioAndHoraFim.execute(request.horaInicio(), request.horaFim());

        List<CampoPersonalizadoEntity> camposPersonalizados =  request.camposPersonalizados() != null ?
                request.camposPersonalizados().stream()
                        .map(cr -> cr.toEntity(tenantId))
                        .toList()
                : null;

        AtividadeDeRelatorioEntity atividadeDeRelatorio = AtividadeDeRelatorioEntity.builder()
                .relatorio(relatorio)
                .descricao(request.descricao())
                .quantidadeRealizada(request.quantidadeRealizada())
                .unidadeMedida(request.unidadeMedida())
                .porcentagemConcluida(request.porcentagemConcluida())
                .horaInicio(request.horaInicio())
                .horaFim(request.horaFim())
                .totalHoras(calculateHorasTotais.execute(request.horaInicio(), request.horaFim(), null))
                .camposPersonalizados(camposPersonalizados)
                .status(request.status())
                .build();

        AtividadeDeRelatorioEntity saved = atividadeDeRelatorioRepository.save(atividadeDeRelatorio);

        List<MaoDeObraDeAtividadeDeRelatorioEntity> maoDeObra = saveMaoDeObraDeAtividade(saved, maoDeObraEntities);
        saved.setMaoDeObra(maoDeObra);

        return CreateAtividadeDeRelatorioResponse.from(saved);
    }

    private List<MaoDeObraEntity> getMaoDeObraDaAtividade(CreateAtividadeDeRelatorioRequest request, Long tenantId, TipoMaoDeObra tipoMaoDeObra) {
        HashSet<Long> maoDeObraIds = new HashSet<>(request.maoDeObraIds());

        if (maoDeObraIds.isEmpty()) {
            return Collections.emptyList();
        }

        return getMaoDeObraListByIdInAndTenantIdAndTypeService.execute(maoDeObraIds, tenantId, tipoMaoDeObra);
    }

    private List<MaoDeObraDeAtividadeDeRelatorioEntity> saveMaoDeObraDeAtividade(AtividadeDeRelatorioEntity atividade, List<MaoDeObraEntity> maoDeObraEntities) {
        List<MaoDeObraDeAtividadeDeRelatorioEntity> toSave = maoDeObraEntities.stream()
                .map(m -> MaoDeObraDeAtividadeDeRelatorioEntity.builder()
                        .atividadeDeRelatorio(atividade)
                        .maoDeObra(m)
                        .funcao(m.getFuncao())
                        .build())
                .toList();

        return maoDeObraDeAtividadeDeRelatorioRepository.saveAll(toSave);
    }
}
