package com.branches.relatorio.service;

import com.branches.arquivo.domain.ArquivoEntity;
import com.branches.atividade.domain.AtividadeDeRelatorioEntity;
import com.branches.comentarios.model.ComentarioDeRelatorioEntity;
import com.branches.equipamento.domain.EquipamentoDeRelatorioEntity;
import com.branches.maodeobra.domain.MaoDeObraDeRelatorioEntity;
import com.branches.material.domain.MaterialDeRelatorioEntity;
import com.branches.material.domain.enums.TipoMaterial;
import com.branches.ocorrencia.domain.OcorrenciaDeRelatorioEntity;
import com.branches.relatorio.domain.AssinaturaDeRelatorioEntity;
import com.branches.relatorio.repository.projections.RelatorioDetailsProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class GenerateRelatorioHtmlService {
    private static final String meuDiarioDeObrasLogoUrl = "https://meudiariodeobras.s3.us-east-2.amazonaws.com/logo-with-name.png";
    private final TemplateEngine templateEngine;

    public String execute(
            RelatorioDetailsProjection details,
            List<OcorrenciaDeRelatorioEntity> ocorrenciasDoRelatorio,
            List<AtividadeDeRelatorioEntity> atividadesDoRelatorio,
            List<EquipamentoDeRelatorioEntity> equipamentosDoRelatorio,
            List<MaoDeObraDeRelatorioEntity> maoDeObraDoRelatorio,
            List<ComentarioDeRelatorioEntity> comentariosDoRelatorio,
            List<MaterialDeRelatorioEntity> materiaisDoRelatorio,
            List<ArquivoEntity> fotosDoRelatorio,
            List<ArquivoEntity> videosDoRelatorio,
            List<AssinaturaDeRelatorioEntity> assinaturas,
            boolean userCanViewCondicoesClimaticas,
            boolean userCanViewHorarioDeTrabalho) {

        // Prepare Thymeleaf context
        Context context = new Context();

        // Add basic details
        context.setVariable("details", details);
        context.setVariable("userCanViewCondicoesClimaticas", userCanViewCondicoesClimaticas);
        context.setVariable("userCanViewHorarioDeTrabalho", userCanViewHorarioDeTrabalho);

        // Process logos
        int logoCount = 0;
        if (details.getLogoDeRelatorio1() != null && details.getLogoDeRelatorio1().getExibir() == Boolean.TRUE) {
            context.setVariable("logo1", details.getLogoDeRelatorio1());
            logoCount++;
        }
        if (details.getLogoDeRelatorio2() != null && details.getLogoDeRelatorio2().getExibir() == Boolean.TRUE) {
            context.setVariable("logo2", details.getLogoDeRelatorio2());
            logoCount++;
        }
        if (details.getLogoDeRelatorio3() != null && details.getLogoDeRelatorio3().getExibir() == Boolean.TRUE) {
            context.setVariable("logo3", details.getLogoDeRelatorio3());
            logoCount++;
        }
        context.setVariable("logoCount", logoCount);

        // Add default logo URL
        context.setVariable("defaultLogoUrl", meuDiarioDeObrasLogoUrl);

        // Add collections
        context.setVariable("ocorrencias", ocorrenciasDoRelatorio);
        context.setVariable("atividades", atividadesDoRelatorio);
        context.setVariable("equipamentos", equipamentosDoRelatorio);
        context.setVariable("maoDeObra", maoDeObraDoRelatorio);
        context.setVariable("comentarios", comentariosDoRelatorio);

        // Split materials by type
        List<MaterialDeRelatorioEntity> materiaisUtilizados = materiaisDoRelatorio != null ?
                materiaisDoRelatorio.stream()
                        .filter(m -> m.getTipoMaterial() == TipoMaterial.UTILIZADO)
                        .collect(Collectors.toList()) : Collections.emptyList();

        List<MaterialDeRelatorioEntity> materiaisRecebidos = materiaisDoRelatorio != null ?
                materiaisDoRelatorio.stream()
                        .filter(m -> m.getTipoMaterial() == TipoMaterial.RECEBIDO)
                        .collect(Collectors.toList()) : Collections.emptyList();

        context.setVariable("materiaisUtilizados", materiaisUtilizados);
        context.setVariable("materiaisRecebidos", materiaisRecebidos);

        // Add photos and videos
        context.setVariable("fotos", fotosDoRelatorio);
        context.setVariable("videos", videosDoRelatorio);

        // Add signatures
        context.setVariable("assinaturas", assinaturas);

        // Render template
        return templateEngine.process("relatorio-obra", context);
    }
}