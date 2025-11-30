package com.branches.arquivo.domain;

import com.branches.arquivo.domain.enums.TipoArquivo;
import com.branches.config.envers.AuditableTenantOwned;
import com.branches.relatorio.domain.RelatorioEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ArquivoEntity extends AuditableTenantOwned {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomeArquivo;

    @Column(nullable = false)
    private String url;

    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoArquivo tipoArquivo;

    @ManyToOne
    @JoinColumn(name = "relatorio_id")
    private RelatorioEntity relatorio;

    public boolean getIsFoto() {
        return this.tipoArquivo == TipoArquivo.FOTO;
    }

    public boolean getIsVideo() {
        return this.tipoArquivo == TipoArquivo.VIDEO;
    }
}
