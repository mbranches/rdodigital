package com.branches.domain;

import com.branches.domain.enums.StatusRelatorio;
import com.branches.shared.enums.TipoMaoDeObra;
import com.branches.shared.config.envers.AuditableTenantOwned;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class RelatorioEntity extends AuditableTenantOwned {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private LocalDate data;
    @Column(nullable = false)
    private Integer numero;
    @Column(nullable = false)
    private Integer prazoContratualObra;
    @Column(nullable = false)
    private Integer prazoDecorridoObra;
    @Column(nullable = false)
    private Integer prazoPraVencerObra;
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
    //todo: adicionar videos e anexos
}
