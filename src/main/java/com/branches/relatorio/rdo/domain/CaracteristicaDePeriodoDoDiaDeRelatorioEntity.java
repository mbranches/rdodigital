package com.branches.relatorio.rdo.domain;

import com.branches.relatorio.rdo.domain.enums.Clima;
import com.branches.relatorio.rdo.domain.enums.CondicaoDoTempo;
import com.branches.relatorio.rdo.domain.enums.PeriodoDoDia;
import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class CaracteristicaDePeriodoDoDiaDeRelatorioEntity {
    @EmbeddedId
    private CondicaoDeTempoDeRelatorioKey id;

    @MapsId("relatorioId")
    @ManyToOne
    @JoinColumn(name = "relatorio_id", nullable = false)
    private RelatorioEntity relatorio;

    @Enumerated(EnumType.STRING)
    @Column(name = "periodo_do_dia", insertable = false, updatable = false, nullable = false)
    private PeriodoDoDia periodoDoDia;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Clima clima;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CondicaoDoTempo condicaoDoTempo;

    @PrePersist
    public void prePersist() {
        if (this.id != null) return;

        this.id = CondicaoDeTempoDeRelatorioKey.of(
                this.getPeriodoDoDia(),
                this.relatorio
        );
    }
}