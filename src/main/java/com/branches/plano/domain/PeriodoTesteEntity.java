package com.branches.plano.domain;

import com.branches.config.envers.Auditable;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class PeriodoTesteEntity extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long tenantId;

    @Column(nullable = false)
    private LocalDateTime dataInicio;
    @Column(nullable = false)
    private LocalDateTime dataFim;

    @Column(nullable = false)
    private Long periodoDias;

    public void finalizar() {
        boolean alreadyFinalized = this.dataFim.isBefore(LocalDateTime.now());

        if (alreadyFinalized) return;

        this.dataFim = LocalDateTime.now();
    }

    public void iniciar(Long periodoDias) {
        this.periodoDias = periodoDias;
        this.dataInicio = LocalDateTime.now();
        this.dataFim = this.dataInicio.plusDays(periodoDias).plusDays(1).minusSeconds(1);
    }

    public boolean isInProgress() {
        return this.dataFim.isAfter(LocalDateTime.now());
    }
}
