package com.branches.maodeobra.domain;

import com.branches.config.envers.AuditableTenantOwned;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Objects;

@SuperBuilder
@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class GrupoMaoDeObraEntity extends AuditableTenantOwned {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 100, nullable = false)
    private String descricao;
    @Column(nullable = false)
    private Boolean ativo;

    public boolean isAtivo() {
        return Boolean.TRUE.equals(ativo);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GrupoMaoDeObraEntity that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
