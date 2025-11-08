package com.branches.relatorio.rdo.domain;

import com.branches.relatorio.rdo.domain.enums.PeriodoDoDia;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@EqualsAndHashCode
@Setter
@Getter
@Embeddable
public class CondicaoDeTempoDeRelatorioKey implements Serializable {
    @Column(nullable = false, name = "periodo_do_dia")
    @Enumerated(EnumType.STRING)
    private PeriodoDoDia periodoDoDia;
    @Column(nullable = false)
    private Long relatorioId;

    public static CondicaoDeTempoDeRelatorioKey of(PeriodoDoDia periodoDoDia, RelatorioEntity relatorio) {
        CondicaoDeTempoDeRelatorioKey id = new CondicaoDeTempoDeRelatorioKey();

        id.setPeriodoDoDia(periodoDoDia);
        id.setRelatorioId(relatorio.getId());

        return id;
    }
}
