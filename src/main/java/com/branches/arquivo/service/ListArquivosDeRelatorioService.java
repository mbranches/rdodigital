package com.branches.arquivo.service;

import com.branches.arquivo.domain.ArquivoEntity;
import com.branches.arquivo.domain.enums.TipoArquivo;
import com.branches.arquivo.dto.response.ArquivoResponse;
import com.branches.arquivo.repository.ArquivoRepository;
import com.branches.exception.InternalServerError;
import com.branches.obra.controller.CheckIfUserHasAccessToObraService;
import com.branches.relatorio.domain.RelatorioEntity;
import com.branches.relatorio.repository.projections.RelatorioWithObraProjection;
import com.branches.relatorio.service.GetRelatorioWithObraByIdExternoAndTenantIdService;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ListArquivosDeRelatorioService {
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final CheckIfConfiguracaoDeRelatorioDaObraPermiteFoto checkIfConfiguracaoDeRelatorioDaObraPermiteFoto;
    private final GetRelatorioWithObraByIdExternoAndTenantIdService getRelatorioWithObraByIdExternoAndTenantIdService;
    private final CheckIfUserCanViewFotosService checkIfUserCanViewFotosService;
    private final ArquivoRepository arquivoRepository;
    private final CheckIfUserHasAccessToObraService checkIfUserHasAccessToObraService;
    private final CheckIfConfiguracaoDeRelatorioDaObraPermiteVideo checkIfConfiguracaoDeRelatorioDaObraPermiteVideo;
    private final CheckIfUserCanViewVideosService checkIfUserCanViewVideosService;

    public List<ArquivoResponse> execute(String relatorioExternalId, String tenantExternalId, TipoArquivo tipo, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);
        
        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        RelatorioWithObraProjection relatorioWithObra = getRelatorioWithObraByIdExternoAndTenantIdService.execute(relatorioExternalId, tenantId);
        RelatorioEntity relatorio = relatorioWithObra.getRelatorio();

        checkIfUserHasAccessToObraService.execute(currentUserTenant, relatorio.getObraId());
        switch (tipo) {
            case FOTO -> {
                checkIfConfiguracaoDeRelatorioDaObraPermiteFoto.execute(relatorioWithObra);
                checkIfUserCanViewFotosService.execute(currentUserTenant);
            }
            case VIDEO -> {
                checkIfConfiguracaoDeRelatorioDaObraPermiteVideo.execute(relatorioWithObra);
                checkIfUserCanViewVideosService.execute(currentUserTenant);
            }

            default -> throw new InternalServerError("Tipo ainda não implementado para exclusão de arquivo de relatorio: " + tipo);

            //todo: quando implementar novos tipos de arquivo, adicionar os devidos cases aqui
        }

        List<ArquivoEntity> response = arquivoRepository.findAllByRelatorioIdAndTipoArquivoOrderByEnversCreatedDateDesc(relatorio.getId(), tipo);

        return response.stream()
                .map(ArquivoResponse::from)
                .toList();
    }
}
