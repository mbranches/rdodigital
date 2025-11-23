package com.branches.relatorio.service;

import com.branches.exception.ForbiddenException;
import com.branches.exception.NotFoundException;
import com.branches.obra.domain.ConfiguracaoRelatoriosEntity;
import com.branches.obra.domain.ObraEntity;
import com.branches.obra.repository.ObraRepository;
import com.branches.relatorio.rdo.domain.*;
import com.branches.relatorio.rdo.domain.enums.StatusRelatorio;
import com.branches.relatorio.rdo.dto.request.*;
import com.branches.relatorio.rdo.repository.*;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import com.branches.utils.CalculateHorasTotais;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UpdateRelatorioService {
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final GetRelatorioByIdExternoAndTenantIdService getRelatorioByIdExternoAndTenantIdService;
    private final CaracteristicaDePeriodoDoDiaRepository caracteristicaDePeriodoDoDiaRepository;
    private final RelatorioRepository relatorioRepository;
    private final UpdateMaoDeObraDeRelatorioService updateMaoDeObraDeRelatorioService;
    private final UpdateEquipamentosDeRelatorioService updateEquipamentosDeRelatorioService;
    private final UpdateAtividadesDeRelatorioService updateAtividadesDeRelatorioService;
    private final UpdateOcorrenciasDeRelatorioService updateOcorrenciasDeRelatorioService;
    private final UpdateComentariosDeRelatorioService updateComentariosDeRelatorioService;
    private final ObraRepository obraRepository;
    private final UpdateMateriaisDeRelatorioService updateMateriaisDeRelatorioService;
    private final CalculateHorasTotais calculateHorasTotais;

    @Transactional
    public void execute(UpdateRelatorioRequest request, String tenantExternalId, String relatorioExternalId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        checkIfUserCanUpdateRelatorio(currentUserTenant);

        RelatorioEntity relatorio = getRelatorioByIdExternoAndTenantIdService.execute(relatorioExternalId, tenantId);

        ObraEntity obra = obraRepository.findById(relatorio.getObraId())
                        .orElseThrow(() -> new NotFoundException("Não foi possível encontra a obra do relatório com id: " + relatorioExternalId));

        ConfiguracaoRelatoriosEntity configuracaoRelatorios = obra.getConfiguracaoRelatorios();

        relatorio.setNumero(request.numeroRelatorio());
        relatorio.setDataInicio(request.dataInicio());
        relatorio.setDataFim(request.dataFim());
        relatorio.setPrazoContratualObra(request.prazoContratual());
        relatorio.setPrazoDecorridoObra(request.prazoDecorrido());
        relatorio.setPrazoPraVencerObra(request.prazoPraVencer());
        updateStatus(currentUserTenant, relatorio, request.status());

        if (configuracaoRelatorios.getShowCondicaoClimatica()) {
            relatorio.setIndiciePluviometrico(request.indicePluviometrico());
            relatorio.setCaracteristicasManha(getUpdatedCaracteristicaEntity(request.caracteristicasManha(), tenantId));
            relatorio.setCaracteristicasTarde(getUpdatedCaracteristicaEntity(request.caracteristicasTarde(), tenantId));
            relatorio.setCaracteristicasNoite(getUpdatedCaracteristicaEntity(request.caracteristicasNoite(), tenantId));
        }

        relatorioRepository.save(relatorio);

        if (configuracaoRelatorios.getShowHorarioDeTrabalho()) {
            relatorio.setHoraInicioTrabalhos(request.horaInicioTrabalhos());
            relatorio.setHoraFimTrabalhos(request.horaFimTrabalhos());
            relatorio.setHorasIntervalo(request.horasIntervalo());
            relatorio.setHorasTrabalhadas(calculateHorasTotais.execute(request.horaInicioTrabalhos(), request.horaFimTrabalhos(), request.horasIntervalo()));

            relatorioRepository.save(relatorio);
        }

        if (configuracaoRelatorios.getShowMaoDeObra()) {
            updateMaoDeObraDeRelatorioService.execute(request.maoDeObra(), relatorio, tenantId);
        }

        if (configuracaoRelatorios.getShowEquipamentos()) {
            updateEquipamentosDeRelatorioService.execute(request.equipamentos(), relatorio, tenantId);
        }

        if (configuracaoRelatorios.getShowAtividades()) {
            updateAtividadesDeRelatorioService.execute(request.atividades(), relatorio, tenantId);
        }

        if (configuracaoRelatorios.getShowOcorrencias()) {
            updateOcorrenciasDeRelatorioService.execute(request.ocorrencias(), relatorio, tenantId);
        }

        if (configuracaoRelatorios.getShowComentarios()) {
            updateComentariosDeRelatorioService.execute(request.comentarios(), relatorio, tenantId);
        }

        if (configuracaoRelatorios.getShowMateriais()) {
            updateMateriaisDeRelatorioService.execute(request.materiais(), relatorio, tenantId);
        }

        //todo: gerar html do relatorio
        //todo: substituir relatorio pdf antigo
        //todo: subir no s3
    }

    private void updateStatus(UserTenantEntity currentUserTenant, RelatorioEntity relatorio, StatusRelatorio status) {
        if (status == StatusRelatorio.APROVADO && !currentUserTenant.getAuthorities().getRelatorios().getCanAprovar()) return;

        relatorio.setStatus(status);
    }

    private CaracteristicaDePeriodoDoDiaEntity getUpdatedCaracteristicaEntity(CaracteristicaDePeriodoDoDiaRequest request, Long tenantId) {
        var id = request.id();

        var entity = caracteristicaDePeriodoDoDiaRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new NotFoundException("Caracteristica de periodo do dia não encontrada com o id: " + id));

        entity.setClima(request.clima());
        entity.setCondicaoDoTempo(request.condicaoDoTempo());
        entity.setAtivo(request.ativo());

        return entity;
    }

    private void checkIfUserCanUpdateRelatorio(UserTenantEntity currentUserTenant) {
        if (currentUserTenant.getAuthorities().getRelatorios().getCanCreateAndEdit()) return;

        throw new ForbiddenException();
    }
}