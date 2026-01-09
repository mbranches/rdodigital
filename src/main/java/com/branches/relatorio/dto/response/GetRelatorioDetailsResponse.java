package com.branches.relatorio.dto.response;

import com.branches.arquivo.domain.ArquivoEntity;
import com.branches.arquivo.dto.response.FileResponse;
import com.branches.arquivo.dto.response.FotoDeRelatorioResponse;
import com.branches.atividade.domain.AtividadeDeRelatorioEntity;
import com.branches.atividade.dto.response.AtividadeDeRelatorioResponse;
import com.branches.comentarios.dto.response.ComentarioDeRelatorioResponse;
import com.branches.comentarios.model.ComentarioDeRelatorioEntity;
import com.branches.condicaoclimatica.dto.response.CaracteristicaDePeriodoDoDiaResponse;
import com.branches.equipamento.domain.EquipamentoDeRelatorioEntity;
import com.branches.equipamento.dto.response.EquipamentoDeRelatorioResponse;
import com.branches.maodeobra.domain.MaoDeObraDeRelatorioEntity;
import com.branches.maodeobra.dto.response.MaoDeObraDeRelatorioResponse;
import com.branches.material.domain.MaterialDeRelatorioEntity;
import com.branches.material.dto.response.MaterialDeRelatorioResponse;
import com.branches.obra.domain.enums.TipoMaoDeObra;
import com.branches.obra.dto.response.ObraByRelatorioResponse;
import com.branches.ocorrencia.domain.OcorrenciaDeRelatorioEntity;
import com.branches.ocorrencia.dto.response.OcorrenciaDeRelatorioResponse;
import com.branches.relatorio.domain.AssinaturaDeRelatorioEntity;
import com.branches.relatorio.domain.enums.StatusRelatorio;
import com.branches.relatorio.repository.projections.RelatorioDetailsProjection;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record GetRelatorioDetailsResponse(
        String id,
        LogoDeRelatorioResponse logo1,
        LogoDeRelatorioResponse logo2,
        LogoDeRelatorioResponse logo3,
        ObraByRelatorioResponse obra,
        String tituloRelatorio,
        TipoMaoDeObra tipoMaoDeObra,
        LocalDate dataInicio,
        LocalDate dataFim,
        LocalTime horaInicioTrabalhos,
        LocalTime horaFimTrabalhos,
        Integer minutosIntervalo,
        LocalTime horasTrabalhadas,
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
        List<MaterialDeRelatorioResponse> materiais,
        List<AssinaturaDeRelatorioResponse> assinaturas,
        List<FotoDeRelatorioResponse> fotos,
        List<FileResponse> videos,
        StatusRelatorio status,
        ModifyerByRelatorioResponse criadoPor,
        ModifyerByRelatorioResponse ultimaModificacao

) {
    public static GetRelatorioDetailsResponse from(RelatorioDetailsProjection relatorioDetails,
                                                   List<OcorrenciaDeRelatorioEntity> ocorrencias,
                                                   List<AtividadeDeRelatorioEntity> atividades,
                                                   List<EquipamentoDeRelatorioEntity> equipamentos,
                                                   List<MaoDeObraDeRelatorioEntity> maoDeObra,
                                                   List<ComentarioDeRelatorioEntity> comentarios,
                                                   List<MaterialDeRelatorioEntity> materiais,
                                                   List<AssinaturaDeRelatorioEntity> assinaturas,
                                                   List<ArquivoEntity> fotos,
                                                   List<ArquivoEntity> videos,
                                                   Boolean canViewCondicaoDoClima,
                                                   Boolean canViewHorarioDeTrabalho) {
        ObraByRelatorioResponse obra = new ObraByRelatorioResponse(relatorioDetails.getObraIdExterno(), relatorioDetails.getObraNome(), relatorioDetails.getObraEndereco(), relatorioDetails.getObraContratante(), relatorioDetails.getObraResponsavel(), relatorioDetails.getObraNumeroContrato());

        String dayOfWeekResponse = relatorioDetails.getDataInicio().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.of("pt", "BR"));

        var caracteristicaManha = canViewCondicaoDoClima ?
                CaracteristicaDePeriodoDoDiaResponse.from(relatorioDetails.getCaracteristicasManha()) : null;
        var caracteristicaTarde = canViewCondicaoDoClima ?
                CaracteristicaDePeriodoDoDiaResponse.from(relatorioDetails.getCaracteristicasTarde()) : null;
        var caracteristicaNoite = canViewCondicaoDoClima ?
                CaracteristicaDePeriodoDoDiaResponse.from(relatorioDetails.getCaracteristicasNoite()) : null;

        var horarioInicioTrabalhos = canViewHorarioDeTrabalho ?
                relatorioDetails.getHoraInicioTrabalhos() : null;
        var horarioFimTrabalhos = canViewHorarioDeTrabalho ?
                relatorioDetails.getHoraFimTrabalhos() : null;
        var minutosIntervalo = canViewHorarioDeTrabalho ?
                relatorioDetails.getMinutosIntervalo() : null;
        var horasTrabalhadas = canViewHorarioDeTrabalho ?
                relatorioDetails.getHorasTrabalhadas() : null;

        var equipamentosResponse = equipamentos != null ?
                equipamentos.stream().map(EquipamentoDeRelatorioResponse::from).toList() : null;
        var atividadesResponse = atividades != null ?
                atividades.stream().map(AtividadeDeRelatorioResponse::from).toList() : null;
        var ocorrenciasResponse = ocorrencias != null ?
                ocorrencias.stream().map(OcorrenciaDeRelatorioResponse::from).toList() : null;
        var maoDeObraResponse = maoDeObra != null ?
                maoDeObra.stream().map(MaoDeObraDeRelatorioResponse::from).toList() : null;
        var comentariosResponse = comentarios != null ?
                comentarios.stream().map(ComentarioDeRelatorioResponse::from).sorted(Comparator.comparing(ComentarioDeRelatorioResponse::dataCriacao).reversed()).toList() : null;
        var materiaisResponse = materiais != null ?
                materiais.stream().map(MaterialDeRelatorioResponse::from).toList() : null;
        var assinaturasResponse = assinaturas != null ?
                assinaturas.stream().map(AssinaturaDeRelatorioResponse::from).toList() : null;

        var logo1 = relatorioDetails.getLogoDeRelatorio1() != null ?
                LogoDeRelatorioResponse.from(relatorioDetails.getLogoDeRelatorio1()) : null;
        var logo2 = relatorioDetails.getLogoDeRelatorio2() != null ?
                LogoDeRelatorioResponse.from(relatorioDetails.getLogoDeRelatorio2()) : null;
        var logo3 = relatorioDetails.getLogoDeRelatorio3() != null ?
                LogoDeRelatorioResponse.from(relatorioDetails.getLogoDeRelatorio3()) : null;

        var fotosResponse = fotos != null ?
                fotos.stream().map(FotoDeRelatorioResponse::from).toList() : null;
        var videosResponse = videos != null ?
                videos.stream().map(FileResponse::from).toList() : null;

        return new GetRelatorioDetailsResponse(
                relatorioDetails.getIdExterno(),
                logo1,
                logo2,
                logo3,
                obra,
                relatorioDetails.getTituloModeloDeRelatorio(),
                relatorioDetails.getTipoMaoDeObra(),
                relatorioDetails.getDataInicio(),
                relatorioDetails.getDataFim(),
                horarioInicioTrabalhos,
                horarioFimTrabalhos,
                minutosIntervalo,
                horasTrabalhadas,
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
                materiaisResponse,
                assinaturasResponse,
                fotosResponse,
                videosResponse,
                relatorioDetails.getStatus(),
                new ModifyerByRelatorioResponse(relatorioDetails.getCriadoPor(), relatorioDetails.getCriadoEm()),
                new ModifyerByRelatorioResponse(relatorioDetails.getUltimaModificacaoPor(), relatorioDetails.getUltimaModificacaoEm())
        );
    }
}
