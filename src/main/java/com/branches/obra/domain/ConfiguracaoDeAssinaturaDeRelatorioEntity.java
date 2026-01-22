package com.branches.obra.domain;

import com.branches.config.envers.AuditableTenantOwned;
import com.branches.relatorio.domain.AssinaturaDeRelatorioEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Setter
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ConfiguracaoDeAssinaturaDeRelatorioEntity extends AuditableTenantOwned {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomeAssinante;

    @OneToMany(mappedBy = "configuracao", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<AssinaturaDeRelatorioEntity> assinaturasDeRelatorio;

    @ManyToOne
    @JoinColumn(name = "configuracao_relatorios_id", nullable = false)
    private ConfiguracaoRelatoriosEntity configuracaoRelatorios;
}
