package com.branches.ocorrencia.domain;

import com.branches.config.envers.AuditableTenantOwned;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class TipoDeOcorrenciaEntity extends AuditableTenantOwned {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100)
    private String descricao;
    @Column(nullable = false)
    private Boolean ativo;

    public boolean isAtivo() {
        return ativo;
    }
}
