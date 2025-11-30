package com.branches.relatorio.repository.projections;

import com.branches.condicaoclimatica.domain.CondicaoClimaticaEntity;
import com.branches.obra.domain.LogoDeRelatorioEntity;
import com.branches.relatorio.domain.enums.StatusRelatorio;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public interface RelatorioDetailsProjection {
    Long getId();
    String getIdExterno();

    LogoDeRelatorioEntity getLogoDeRelatorio1();
    LogoDeRelatorioEntity getLogoDeRelatorio2();
    LogoDeRelatorioEntity getLogoDeRelatorio3();

    String getObraIdExterno();
    String getObraNome();
    String getObraEndereco();
    String getObraContratante();
    String getObraResponsavel();
    String getObraNumeroContrato();

    String getTituloModeloDeRelatorio();

    Boolean getShowAtividades();
    Boolean getShowCondicaoClimatica();
    Boolean getShowComentarios();
    Boolean getShowEquipamentos();
    Boolean getShowMaoDeObra();
    Boolean getShowOcorrencias();
    Boolean getShowMateriais();
    Boolean getShowHorarioDeTrabalho();
    Boolean getShowFotos();
    Boolean getShowVideos();

    LocalDate getDataInicio();
    LocalDate getDataFim();

    LocalTime getHoraInicioTrabalhos();
    LocalTime getHoraFimTrabalhos();
    LocalTime getHorasIntervalo();
    LocalTime getHorasTrabalhadas();

    Long getNumero();

    Long getPrazoContratual();
    Long getPrazoDecorrido();
    Long getPrazoPraVencer();

    CondicaoClimaticaEntity getCaracteristicasManha();
    CondicaoClimaticaEntity getCaracteristicasTarde();
    CondicaoClimaticaEntity getCaracteristicasNoite();
    BigDecimal getIndicePluviometrico();

    StatusRelatorio getStatus();

    String getCriadoPor();
    LocalDateTime getCriadoEm();
    String getUltimaModificacaoPor();
    LocalDateTime getUltimaModificacaoEm();
}
