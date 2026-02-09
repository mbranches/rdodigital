package com.branches.arquivo.service;

import com.branches.arquivo.domain.ArquivoEntity;
import com.branches.arquivo.domain.enums.TipoArquivo;
import com.branches.arquivo.dto.UpdateArquivoRequest;
import com.branches.arquivo.repository.ArquivoRepository;
import com.branches.exception.InternalServerError;
import com.branches.obra.controller.CheckIfUserHasAccessToObraService;
import com.branches.relatorio.domain.RelatorioEntity;
import com.branches.relatorio.repository.projections.RelatorioWithObraProjection;
import com.branches.relatorio.service.CheckIfUserHasAccessToEditRelatorioService;
import com.branches.relatorio.service.GetRelatorioWithObraByIdExternoAndTenantIdService;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UpdateArquivoDeRelatorioService {
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final GetRelatorioWithObraByIdExternoAndTenantIdService getRelatorioWithObraByIdExternoAndTenantIdService;
    private final CheckIfUserCanViewFotosService checkIfUserCanViewFotosService;
    private final CheckIfConfiguracaoDeRelatorioDaObraPermiteFoto checkIfConfiguracaoDeRelatorioDaObraPermiteFoto;
    private final GetArquivoDeRelatorioByIdAndRelatorioIdService getArquivoDeRelatorioByIdAndRelatorioIdAndTipoService;
    private final ArquivoRepository arquivoRepository;
    private final CheckIfUserHasAccessToEditRelatorioService checkIfUserHasAccessToEditRelatorioService;
    private final CheckIfUserHasAccessToObraService checkIfUserHasAccessToObraService;
    private final CheckIfConfiguracaoDeRelatorioDaObraPermiteVideo checkIfConfiguracaoDeRelatorioDaObraPermiteVideo;
    private final CheckIfUserCanViewVideosService checkIfUserCanViewVideosService;

    public void execute(UpdateArquivoRequest request, Long arquivoId, String relatorioExternalId, String tenantExternalId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        RelatorioWithObraProjection relatorioWithObra = getRelatorioWithObraByIdExternoAndTenantIdService.execute(relatorioExternalId, tenantId);
        RelatorioEntity relatorio = relatorioWithObra.getRelatorio();

        ArquivoEntity toEdit = getArquivoDeRelatorioByIdAndRelatorioIdAndTipoService.execute(arquivoId, relatorio.getId(), TipoArquivo.FOTO);
        switch (toEdit.getTipoArquivo()) {
            case FOTO -> {
                checkIfConfiguracaoDeRelatorioDaObraPermiteFoto.execute(relatorioWithObra.getObra());
                checkIfUserCanViewFotosService.execute(currentUserTenant);
            }
            case VIDEO -> {
                checkIfConfiguracaoDeRelatorioDaObraPermiteVideo.execute(relatorioWithObra.getObra());
                checkIfUserCanViewVideosService.execute(currentUserTenant);
            }

            default -> throw new InternalServerError("Tipo ainda não implementado para edição de arquivo de relatorio: " + toEdit.getTipoArquivo());

            //todo: quando implementar novos tipos de arquivo, adicionar os devidos cases aqui
        }

        checkIfUserHasAccessToObraService.execute(currentUserTenant, relatorio.getObraId());
        checkIfUserHasAccessToEditRelatorioService.execute(currentUserTenant, relatorio.getStatus());

        toEdit.setDescricao(request.descricao());

        arquivoRepository.save(toEdit);
    }
}
