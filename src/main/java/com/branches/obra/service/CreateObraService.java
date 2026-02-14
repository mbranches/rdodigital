package com.branches.obra.service;

import com.branches.assinaturadeplano.domain.AssinaturaDePlanoEntity;
import com.branches.assinaturadeplano.service.GetAssinaturaActiveByTenantIdService;
import com.branches.configuradores.domain.ModeloDeRelatorioEntity;
import com.branches.configuradores.service.GetModeloDeRelatorioByIdAndTenantIdService;
import com.branches.obra.domain.ConfiguracaoRelatoriosEntity;
import com.branches.obra.domain.GrupoDeObraEntity;
import com.branches.obra.domain.ObraEntity;
import com.branches.obra.domain.enums.StatusObra;
import com.branches.obra.dto.request.CreateObraRequest;
import com.branches.obra.dto.response.CreateObraResponse;
import com.branches.obra.repository.ObraRepository;
import com.branches.exception.BadRequestException;
import com.branches.exception.ForbiddenException;
import com.branches.plano.domain.PeriodoTesteEntity;
import com.branches.plano.service.FindTenantPeriodoTesteService;
import com.branches.tenant.domain.TenantEntity;
import com.branches.tenant.service.GetTenantByIdExternoService;
import com.branches.usertenant.domain.UserObraPermitidaEntity;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.repository.UserObraPermitidaRepository;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Transactional
@RequiredArgsConstructor
@Service
public class CreateObraService {
    private final GetAssinaturaActiveByTenantIdService getAssinaturaActiveByTenantIdService;
    private final ObraRepository obraRepository;
    private final GetGrupoDeObraByIdAndTenantIdService getGrupoDeObraByIdAndTenantIdService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final GetTenantByIdExternoService getTenantByIdExternoService;
    private final GetModeloDeRelatorioByIdAndTenantIdService getModeloDeRelatorioByIdAndTenantIdService;
    private final FindTenantPeriodoTesteService findTenantPeriodoTesteService;
    private final UserObraPermitidaRepository userObraPermitidaRepository;

    public CreateObraResponse execute(CreateObraRequest request, String tenantDaObraExternalId, List<UserTenantEntity> userTenants) {
        TenantEntity tenant = getTenantByIdExternoService.execute(tenantDaObraExternalId);

        Long tenantId = tenant.getId();

        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        verifyIfUserHasPermissionToCreateObra(currentUserTenant);

        verifyIfPlanoAllowsCreateObra(tenantId);

        ModeloDeRelatorioEntity modeloDeRelatorio = getModeloDeRelatorioByIdAndTenantIdService.execute(request.modeloDeRelatorioId(), tenantId);
        ConfiguracaoRelatoriosEntity configuracaoRelatorios = ConfiguracaoRelatoriosEntity.by(modeloDeRelatorio, tenant.getLogoUrl(), tenant.getNome(), request.contratante(), tenantId);

        ObraEntity obraToSave = ObraEntity.builder()
                .nome(request.nome())
                .responsavel(request.responsavel())
                .contratante(request.contratante())
                .tipoContrato(request.tipoContrato())
                .dataInicio(request.dataInicio())
                .dataPrevistaFim(request.dataPrevistaFim())
                .numeroContrato(request.numeroContrato())
                .endereco(request.endereco())
                .observacoes(request.observacoes())
                .tipoMaoDeObra(request.tipoMaoDeObra())
                .status(request.status())
                .configuracaoRelatorios(configuracaoRelatorios)
                .tenantId(tenantId)
                .build();

        if (request.grupoId() != null) {
            GrupoDeObraEntity grupo = getGrupoDeObraByIdAndTenantIdService.execute(request.grupoId(), tenantId);

            obraToSave.setGrupo(grupo);
        }

        if (request.status() == StatusObra.CONCLUIDA) {
            obraToSave.setDataFimReal(LocalDate.now());
        }

        ObraEntity savedObra = obraRepository.save(obraToSave);

        if (!currentUserTenant.isAdministrador()) {
            UserObraPermitidaEntity obraPermitidaEntity = UserObraPermitidaEntity.builder()
                    .userTenant(currentUserTenant)
                    .obraId(savedObra.getId())
                    .build();
            obraPermitidaEntity.setarId();

            userObraPermitidaRepository.save(obraPermitidaEntity);
        }

        return CreateObraResponse.from(savedObra);
    }

    private void verifyIfUserHasPermissionToCreateObra(UserTenantEntity currentUserTenant) {
        Boolean userCanCreateOrEdit = currentUserTenant.getAuthorities().getObras().getCanCreateAndEdit();

        if (!userCanCreateOrEdit) {
            throw new ForbiddenException();
        }
    }

    private void verifyIfPlanoAllowsCreateObra(Long tenantId) {
        Optional<PeriodoTesteEntity> optionalPeriodoTeste = findTenantPeriodoTesteService.execute(tenantId);

        if (optionalPeriodoTeste.isPresent() && optionalPeriodoTeste.get().isInProgress()) {
            return;
        }

        Integer quantityObrasActive = obraRepository.countByTenantIdAndAtivoIsTrue(tenantId);

        AssinaturaDePlanoEntity assinaturaAtiva = getAssinaturaActiveByTenantIdService.execute(tenantId);

        if (assinaturaAtiva.getPlano().getLimiteObras() - quantityObrasActive <= 0) {
            throw new BadRequestException("Limite de obras atingido");
        }
    }
}
