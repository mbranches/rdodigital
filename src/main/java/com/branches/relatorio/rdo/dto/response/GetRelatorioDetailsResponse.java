package com.branches.relatorio.rdo.dto.response;

import com.branches.relatorio.rdo.domain.*;
import com.branches.relatorio.rdo.domain.enums.StatusRelatorio;
import com.branches.relatorio.rdo.repository.projections.RelatorioDetailsProjection;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Builder
public record GetRelatorioDetailsResponse(
        String id,
        String tenantLogoUrl,
        ObraByRelatorioResponse obra,
        LocalDate data,
        String diaDaSemana,
        Long numeroRelatorio,
        Long prazoContratual,
        Long prazoDecorrido,
        Long prazoPraVencer,
        CaracteristicaDePeriodoDoDiaResponse caracteristicaManha,
        CaracteristicaDePeriodoDoDiaResponse caracteristicaTarde,
        CaracteristicaDePeriodoDoDiaResponse caracteristicaNoite,
        BigDecimal indicePluviometrico,
        List<MaoDeObraDeRelatorioResponse> maoDeObra,
        List<EquipamentoDeRelatorioResponse> equipamentos,
        List<AtividadeDeRelatorioResponse> atividades,
        List<OcorrenciaDeRelatorioResponse> ocorrencias,
        List<ComentarioDeRelatorioResponse> comentarios,
        //todo:adicionar fotos
        StatusRelatorio status,
        ModifyerByRelatorioResponse criadoPor,
        ModifyerByRelatorioResponse ultimaModificacao

) {
    public static GetRelatorioDetailsResponse from(RelatorioDetailsProjection relatorioDetails, List<OcorrenciaDeRelatorioEntity> ocorrencias, List<AtividadeDeRelatorioEntity> atividades, List<EquipamentoDeRelatorioEntity> equipamentos, List<MaoDeObraDeRelatorioEntity> maoDeObra, List<ComentarioDeRelatorioEntity> comentarios, Boolean canViewCondicaoDoClima) {
        ObraByRelatorioResponse obra = new ObraByRelatorioResponse(relatorioDetails.getObraIdExterno(), relatorioDetails.getObraNome(), relatorioDetails.getObraEndereco(), relatorioDetails.getObraContratante(), relatorioDetails.getObraResponsavel(), relatorioDetails.getObraNumeroContrato());

        String dayOfWeekResponse = relatorioDetails.getData().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.of("pt", "BR"));

        var caracteristicaManha = canViewCondicaoDoClima ?
                CaracteristicaDePeriodoDoDiaResponse.from(relatorioDetails.getCaracteristicasManha()) : null;
        var caracteristicaTarde = canViewCondicaoDoClima ?
                CaracteristicaDePeriodoDoDiaResponse.from(relatorioDetails.getCaracteristicasTarde()) : null;
        var caracteristicaNoite = canViewCondicaoDoClima ?
                CaracteristicaDePeriodoDoDiaResponse.from(relatorioDetails.getCaracteristicasNoite()) : null;

        var equipamentosResponse = equipamentos != null ?
                equipamentos.stream().map(EquipamentoDeRelatorioResponse::from).toList() : null;
        var atividadesResponse = atividades != null ?
                atividades.stream().map(AtividadeDeRelatorioResponse::from).toList() : null;
        var ocorrenciasResponse = ocorrencias != null ?
                ocorrencias.stream().map(OcorrenciaDeRelatorioResponse::from).toList() : null;
        var maoDeObraResponse = maoDeObra != null ?
                maoDeObra.stream().map(MaoDeObraDeRelatorioResponse::from).toList() : null;
        var comentariosResponse = comentarios != null ?
                comentarios.stream().map(ComentarioDeRelatorioResponse::from).toList() : null;

        return new GetRelatorioDetailsResponse(
                relatorioDetails.getIdExterno(),
                relatorioDetails.getTenantLogoUrl(),
                obra,
                relatorioDetails.getData(),
                dayOfWeekResponse,
                relatorioDetails.getNumero(),
                relatorioDetails.getPrazoContratual(),
                relatorioDetails.getPrazoDecorrido(),
                relatorioDetails.getPrazoPraVencer(),
                caracteristicaManha,
                caracteristicaTarde,
                caracteristicaNoite,
                relatorioDetails.getIndicePluviometrico(),
                maoDeObraResponse,
                equipamentosResponse,
                atividadesResponse,
                ocorrenciasResponse,
                comentariosResponse,
                relatorioDetails.getStatus(),
                new ModifyerByRelatorioResponse(relatorioDetails.getCriadoPor(), relatorioDetails.getCriadoEm()),
                new ModifyerByRelatorioResponse(relatorioDetails.getUltimaModificacaoPor(), relatorioDetails.getUltimaModificacaoEm())
        );
    }
}
