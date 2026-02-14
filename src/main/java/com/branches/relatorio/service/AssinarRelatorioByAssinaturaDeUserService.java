package com.branches.relatorio.service;

import com.branches.exception.BadRequestException;
import com.branches.exception.NotFoundException;
import com.branches.obra.service.CheckIfUserHasAccessToObraService;
import com.branches.relatorio.domain.AssinaturaDeRelatorioEntity;
import com.branches.relatorio.domain.RelatorioEntity;
import com.branches.relatorio.repository.AssinaturaDeRelatorioRepository;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.user.domain.UserEntity;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class AssinarRelatorioByAssinaturaDeUserService {
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final CheckIfUserHasAccessToEditRelatorioService checkIfUserHasAccessToEditRelatorioService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final AssinaturaDeRelatorioRepository assinaturaDeRelatorioRepository;
    private final CheckIfUserHasAccessToObraService checkIfUserHasAccessToObraService;
    private final GetRelatorioByIdExternoAndTenantIdService getRelatorioByIdExternoAndTenantIdService;

    public void execute(Long id, String relatorioExternalId, String tenantExternalId, UserEntity user) {
        List<UserTenantEntity> userTenants = user.getUserTenantEntities();

        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);
        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        RelatorioEntity relatorio = getRelatorioByIdExternoAndTenantIdService.execute(relatorioExternalId, tenantId);

        checkIfUserHasAccessToObraService.execute(currentUserTenant, relatorio.getObraId());
        checkIfUserHasAccessToEditRelatorioService.execute(currentUserTenant, relatorio.getStatus());

        AssinaturaDeRelatorioEntity assinaturaDeRelatorioEntity = relatorio.getAssinaturas().stream()
                .filter(ass -> ass.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Assinatura de relatório não encontrado com id: " + id));

        checkIfUserHasAssinaturaCadastrada(user);
        assinaturaDeRelatorioEntity.setAssinaturaUrl(user.getAssinaturaUrl());
        assinaturaDeRelatorioRepository.save(assinaturaDeRelatorioEntity);
    }

    private void checkIfUserHasAssinaturaCadastrada(UserEntity userEntity) {
        if (userEntity.getAssinaturaUrl() != null && !userEntity.getAssinaturaUrl().isEmpty()) return;

        throw new BadRequestException("Usuário não possui assinatura cadastrada");
    }
}
