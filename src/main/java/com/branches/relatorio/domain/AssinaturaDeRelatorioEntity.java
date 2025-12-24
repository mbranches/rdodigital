package com.branches.relatorio.domain;

import com.branches.obra.domain.ConfiguracaoDeAssinaturaDeRelatorioEntity;
import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class AssinaturaDeRelatorioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "configuracao_id", nullable = false)
    private ConfiguracaoDeAssinaturaDeRelatorioEntity configuracao;

    @Column(name = "assinatura_url", columnDefinition = "TEXT")
    private String assinaturaUrl;

    @ManyToOne
    @JoinColumn(name = "relatorio_id", nullable = false)
    private RelatorioEntity relatorio;
}
