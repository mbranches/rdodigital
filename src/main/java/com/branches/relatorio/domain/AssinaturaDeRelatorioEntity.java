package com.branches.relatorio.domain;

import com.branches.config.envers.AuditableTenantOwned;
import com.branches.obra.domain.ConfiguracaoDeAssinaturaDeRelatorioEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class AssinaturaDeRelatorioEntity extends AuditableTenantOwned {
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
