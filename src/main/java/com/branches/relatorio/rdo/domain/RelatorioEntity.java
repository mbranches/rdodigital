package com.branches.relatorio.rdo.domain;

import com.branches.relatorio.rdo.domain.enums.StatusRelatorio;
import com.branches.obra.domain.enums.TipoMaoDeObra;
import com.branches.config.envers.AuditableTenantOwned;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
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
    private LocalDate data;

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

    @Column(length = 10)
    private String indiciePluviometrico;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "caracteristicas_manha_id")
    private CaracteristicaDePeriodoDoDiaEntity caracteristicasManha;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "caracteristicas_tarde_id")
    private CaracteristicaDePeriodoDoDiaEntity caracteristicasTarde;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "caracteristicas_noite_id")
    private CaracteristicaDePeriodoDoDiaEntity caracteristicasNoite;

    //todo: adicionar videos e anexos
}
