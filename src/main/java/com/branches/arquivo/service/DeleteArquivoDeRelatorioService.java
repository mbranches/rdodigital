package com.branches.arquivo.service;

import com.branches.arquivo.domain.ArquivoEntity;
import com.branches.arquivo.repository.ArquivoRepository;
import com.branches.exception.InternalServerError;
import com.branches.exception.NotFoundException;
import com.branches.external.aws.S3DeleteFile;
import com.branches.obra.service.CheckIfUserHasAccessToObraService;
import com.branches.relatorio.domain.RelatorioEntity;
import com.branches.relatorio.repository.projections.RelatorioWithObraProjection;
import com.branches.relatorio.service.CheckIfUserHasAccessToEditRelatorioService;
import com.branches.relatorio.service.GetRelatorioWithObraByIdExternoAndTenantIdService;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class DeleteArquivoDeRelatorioService {
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final GetRelatorioWithObraByIdExternoAndTenantIdService getRelatorioWithObraByIdExternoAndTenantIdService;
    private final ArquivoRepository arquivoRepository;
    private final CheckIfUserCanViewFotosService checkIfUserCanViewFotosService;
    private final CheckIfConfiguracaoDeRelatorioDaObraPermiteFoto checkIfConfiguracaoDeRelatorioDaObraPermiteFoto;
    private final CheckIfConfiguracaoDeRelatorioDaObraPermiteVideo checkIfConfiguracaoDeRelatorioDaObraPermiteVideo;
    private final CheckIfUserCanViewVideosService checkIfUserCanViewVideosService;
    private final CheckIfUserHasAccessToEditRelatorioService checkIfUserHasAccessToEditRelatorioService;
    private final CheckIfUserHasAccessToObraService checkIfUserHasAccessToObraService;
    private final S3DeleteFile s3DeleteFile;

    public void execute(Long arquivoId, String relatorioExternalId, String tenantExternalId, List<UserTenantEntity> userTenants) {
        log.info("Iniciando delete arquivo de relatório. arquivoId: {}, relatorioExternalId: {}, tenantExternalId: {}",
                arquivoId, relatorioExternalId, tenantExternalId);
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        RelatorioWithObraProjection relatorioWithObra = getRelatorioWithObraByIdExternoAndTenantIdService.execute(relatorioExternalId, tenantId);
        RelatorioEntity relatorio = relatorioWithObra.getRelatorio();

        checkIfUserHasAccessToObraService.execute(currentUserTenant, relatorio.getObraId());

        ArquivoEntity arquivoEntity = arquivoRepository.findByIdAndRelatorioId(arquivoId, relatorio.getId())
                .orElseThrow(() -> new NotFoundException("Arquivo de relatorio nao encontrado com o id: " + arquivoId));

        switch (arquivoEntity.getTipoArquivo()) {
            case FOTO -> {
                checkIfConfiguracaoDeRelatorioDaObraPermiteFoto.execute(relatorioWithObra.getObra());
                checkIfUserCanViewFotosService.execute(currentUserTenant);
            }
            case VIDEO -> {
                checkIfConfiguracaoDeRelatorioDaObraPermiteVideo.execute(relatorioWithObra.getObra());
                checkIfUserCanViewVideosService.execute(currentUserTenant);
            }

            default -> throw new InternalServerError("Tipo ainda não implementado para exclusão de arquivo de relatorio: " + arquivoEntity.getTipoArquivo());

            //todo: quando implementar novos tipos de arquivo, adicionar os devidos cases aqui
        }
        checkIfUserHasAccessToEditRelatorioService.execute(currentUserTenant, relatorio.getStatus());

        arquivoRepository.delete(arquivoEntity);

        try {
            log.info("Arquivo de relatório deletado com sucesso.");
            log.info("Iniciando exclusão do arquivo físico associado ao arquivo de relatório. link: {}", arquivoEntity.getUrl());

            s3DeleteFile.execute(arquivoEntity.getUrl());
        } catch (Exception e) {
            log.error("Erro ao deletar o arquivo físico associado ao arquivo de relatório. link: {}. Erro: {}", arquivoEntity.getUrl(), e.getMessage());
        }
    }
}
