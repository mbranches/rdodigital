package com.branches.relatorio.service;

import com.branches.arquivo.domain.ArquivoEntity;
import com.branches.atividade.domain.AtividadeDeRelatorioEntity;
import com.branches.comentarios.model.ComentarioDeRelatorioEntity;
import com.branches.equipamento.domain.EquipamentoDeRelatorioEntity;
import com.branches.external.aws.S3UploadFile;
import com.branches.maodeobra.domain.MaoDeObraDeRelatorioEntity;
import com.branches.material.domain.MaterialDeRelatorioEntity;
import com.branches.ocorrencia.domain.OcorrenciaDeRelatorioEntity;
import com.branches.relatorio.domain.AssinaturaDeRelatorioEntity;
import com.branches.relatorio.repository.projections.RelatorioDetailsProjection;
import com.branches.usertenant.domain.PermissionsItensDeRelatorio;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.utils.FileContentType;
import com.branches.utils.HtmlToPdfConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class GenerateRelatorioFileService {
    private final GenerateRelatorioHtmlService generateRelatorioHtmlService;
    private final HtmlToPdfConverter htmlToPdfConverter;
    private final S3UploadFile s3UploadFile;

    public String execute(
            RelatorioDetailsProjection relatorioDetails,
            UserTenantEntity userTenant,
            List<OcorrenciaDeRelatorioEntity> ocorrenciasDoRelatorio,
            List<AtividadeDeRelatorioEntity> atividadesDoRelatorio,
            List<EquipamentoDeRelatorioEntity> equipamentosDoRelatorio,
            List<MaoDeObraDeRelatorioEntity> maoDeObraDoRelatorio,
            List<ComentarioDeRelatorioEntity> comentariosDoRelatorio,
            List<MaterialDeRelatorioEntity> materiaisDoRelatorio,
            List<ArquivoEntity> fotosDoRelatorio,
            List<ArquivoEntity> videosDoRelatorio,
            List<AssinaturaDeRelatorioEntity> assinaturas
    ) {
        PermissionsItensDeRelatorio userPermissionsItensDeRelatorio = userTenant.getAuthorities().getItensDeRelatorio();

        String html = generateRelatorioHtmlService.execute(
                relatorioDetails,
                ocorrenciasDoRelatorio,
                atividadesDoRelatorio,
                equipamentosDoRelatorio,
                maoDeObraDoRelatorio,
                comentariosDoRelatorio,
                materiaisDoRelatorio,
                fotosDoRelatorio,
                videosDoRelatorio,
                assinaturas,
                userPermissionsItensDeRelatorio.getCondicaoDoClima(),
                userPermissionsItensDeRelatorio.getHorarioDeTrabalho()
        );

        byte[] pdfBytes = htmlToPdfConverter.execute(html);

        String fileName = "relatorio-" + LocalDateTime.now() + ".pdf";
        String path = "tenants/%s/obras/%s/relatorios/%s".formatted(relatorioDetails.getTenantIdExterno(), relatorioDetails.getObraIdExterno(), relatorioDetails.getIdExterno());

        return s3UploadFile.execute(fileName, path, pdfBytes, FileContentType.PDF);
    }
}