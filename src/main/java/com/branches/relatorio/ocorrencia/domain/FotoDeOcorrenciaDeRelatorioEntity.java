package com.branches.relatorio.ocorrencia.domain;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class FotoDeOcorrenciaDeRelatorioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String url;
    @ManyToOne
    @JoinColumn(name = "ocorrencia_de_relatorio_id", nullable = false)
    private OcorrenciaDeRelatorioEntity ocorrenciaDeRelatorio;
}
