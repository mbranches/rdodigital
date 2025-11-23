package com.branches.relatorio.repository.projections;

import com.branches.relatorio.domain.CaracteristicaDePeriodoDoDiaEntity;
import com.branches.relatorio.domain.enums.StatusRelatorio;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public interface RelatorioDetailsProjection {
    Long getId();
    String getIdExterno();

    String getTenantLogoUrl();

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

    CaracteristicaDePeriodoDoDiaEntity getCaracteristicasManha();
    CaracteristicaDePeriodoDoDiaEntity getCaracteristicasTarde();
    CaracteristicaDePeriodoDoDiaEntity getCaracteristicasNoite();
    BigDecimal getIndicePluviometrico();

    StatusRelatorio getStatus();

    String getCriadoPor();
    LocalDateTime getCriadoEm();
    String getUltimaModificacaoPor();
    LocalDateTime getUltimaModificacaoEm();
}
