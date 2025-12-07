package com.branches.arquivo.service;

import com.branches.arquivo.domain.ArquivoEntity;
import com.branches.arquivo.domain.enums.TipoArquivo;
import com.branches.arquivo.dto.UpdateArquivoRequest;
import com.branches.arquivo.repository.ArquivoRepository;
import com.branches.relatorio.repository.projections.RelatorioWithObraProjection;
import com.branches.relatorio.service.CheckIfUserHasAccessToEditRelatorioService;
import com.branches.relatorio.service.GenerateRelatorioFileToUsersService;
import com.branches.relatorio.service.GetRelatorioWithObraByIdExternoAndTenantIdService;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import com.branches.utils.ItemRelatorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UpdateFotoDeRelatorioService {
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final GetRelatorioWithObraByIdExternoAndTenantIdService getRelatorioWithObraByIdExternoAndTenantIdService;
    private final CheckIfUserCanViewFotosService checkIfUserCanViewFotosService;
    private final CheckIfConfiguracaoDeRelatorioDaObraPermiteFoto checkIfConfiguracaoDeRelatorioDaObraPermiteFoto;
    private final GetArquivoDeRelatorioByIdAndRelatorioIdService getArquivoDeRelatorioByIdAndRelatorioIdAndTipoService;
    private final ArquivoRepository arquivoRepository;
    private final CheckIfUserHasAccessToEditRelatorioService checkIfUserHasAccessToEditRelatorioService;
    private final GenerateRelatorioFileToUsersService generateRelatorioFileToUsersService;

    public void execute(UpdateArquivoRequest request, Long arquivoId, String relatorioExternalId, String tenantExternalId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        RelatorioWithObraProjection relatorioWithObra = getRelatorioWithObraByIdExternoAndTenantIdService.execute(relatorioExternalId, tenantId);

        checkIfConfiguracaoDeRelatorioDaObraPermiteFoto.execute(relatorioWithObra);
        checkIfUserCanViewFotosService.execute(currentUserTenant);
        checkIfUserHasAccessToEditRelatorioService.execute(currentUserTenant, relatorioWithObra.getRelatorio().getStatus());

        ArquivoEntity toEdit = getArquivoDeRelatorioByIdAndRelatorioIdAndTipoService.execute(arquivoId, relatorioWithObra.getRelatorio().getId(), TipoArquivo.FOTO);
        toEdit.setDescricao(request.descricao());

        arquivoRepository.save(toEdit);

        generateRelatorioFileToUsersService.executeOnlyToNecessaryUsers(relatorioWithObra.getRelatorio().getId(), ItemRelatorio.FOTOS);
    }
}
