package com.branches.relatorio.domain;

import com.branches.relatorio.domain.enums.StatusRelatorio;
import com.branches.obra.domain.enums.TipoMaoDeObra;
import com.branches.config.envers.AuditableTenantOwned;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Setter
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class RelatorioEntity extends AuditableTenantOwned {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Builder.Default
    @Column(nullable = false, unique = true)
    private String idExterno = UUID.randomUUID().toString();

    @Column(nullable = false)
    private LocalDate dataInicio;
    private LocalDate dataFim;

    private LocalTime horaInicioTrabalhos;
    private LocalTime horaFimTrabalhos;
    private LocalTime horasIntervalo;
    private LocalTime horasTrabalhadas;

    @Column(nullable = false)
    private Long numero;

    @Column(nullable = false)
    private Long prazoContratualObra;
    @Column(nullable = false)
    private Long prazoDecorridoObra;
    @Column(nullable = false)
    private Long prazoPraVencerObra;

    private String pdfUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusRelatorio status;

    @Column(nullable = false)
    private Long obraId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMaoDeObra tipoMaoDeObra;

    @Column(precision = 10, scale = 2)
    private BigDecimal indiciePluviometrico;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "caracteristicas_manha_id")
    private CaracteristicaDePeriodoDoDiaEntity caracteristicasManha;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "caracteristicas_tarde_id")
    private CaracteristicaDePeriodoDoDiaEntity caracteristicasTarde;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "caracteristicas_noite_id")
    private CaracteristicaDePeriodoDoDiaEntity caracteristicasNoite;

    @Builder.Default
    @Column(nullable = false)
    private Boolean ativo = true;

    //todo: adicionar videos e anexos

    public void setInativo() {
        this.ativo = false;
    }
}
