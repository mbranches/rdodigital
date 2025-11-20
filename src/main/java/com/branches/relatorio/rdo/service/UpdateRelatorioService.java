package com.branches.relatorio.rdo.service;

import com.branches.exception.ForbiddenException;
import com.branches.exception.NotFoundException;
import com.branches.relatorio.rdo.domain.*;
import com.branches.relatorio.rdo.domain.enums.StatusRelatorio;
import com.branches.relatorio.rdo.dto.request.*;
import com.branches.relatorio.rdo.repository.*;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
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

    @Transactional
    public void execute(UpdateRelatorioRequest request, String tenantExternalId, String relatorioExternalId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        checkIfUserCanUpdateRelatorio(currentUserTenant);

        RelatorioEntity relatorio = getRelatorioByIdExternoAndTenantIdService.execute(relatorioExternalId, tenantId);
        relatorio.setNumero(request.numeroRelatorio());
        relatorio.setData(request.data());
        relatorio.setPrazoContratualObra(request.prazoContratual());
        relatorio.setPrazoDecorridoObra(request.prazoDecorrido());
        relatorio.setPrazoPraVencerObra(request.prazoPraVencer());
        relatorio.setIndiciePluviometrico(request.indicePluviometrico());
        updateStatus(currentUserTenant, relatorio, request.status());
        relatorio.setCaracteristicasManha(getUpdatedCaracteristicaEntity(request.caracteristicasManha(), tenantId));
        relatorio.setCaracteristicasTarde(getUpdatedCaracteristicaEntity(request.caracteristicasTarde(), tenantId));
        relatorio.setCaracteristicasNoite(getUpdatedCaracteristicaEntity(request.caracteristicasNoite(), tenantId));

        relatorioRepository.save(relatorio);

        updateMaoDeObraDeRelatorioService.execute(request.maoDeObra(), relatorio, tenantId);
        updateEquipamentosDeRelatorioService.execute(request.equipamentos(), relatorio, tenantId);
        updateAtividadesDeRelatorioService.execute(request.atividades(), relatorio, tenantId);
        updateOcorrenciasDeRelatorioService.execute(request.ocorrencias(), relatorio, tenantId);
        updateComentariosDeRelatorioService.execute(request.comentarios(), relatorio, tenantId);

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