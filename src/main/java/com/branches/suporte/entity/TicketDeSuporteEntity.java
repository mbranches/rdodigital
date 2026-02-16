package com.branches.suporte.entity;

import com.branches.config.envers.AuditableTenantOwned;
import com.branches.suporte.entity.enums.TipoSuporte;
import com.branches.user.domain.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class TicketDeSuporteEntity extends AuditableTenantOwned {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoSuporte tipoSuporte;

    @Column(nullable = false)
    private String assunto;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @Column(nullable = false)
    private Boolean enviado;

}
