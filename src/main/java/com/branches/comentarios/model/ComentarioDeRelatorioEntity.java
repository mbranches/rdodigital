package com.branches.comentarios.model;

import com.branches.config.envers.AuditableTenantOwned;
import com.branches.relatorio.domain.RelatorioEntity;
import com.branches.user.domain.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Setter
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ComentarioDeRelatorioEntity extends AuditableTenantOwned {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String descricao;
    @ManyToOne
    @JoinColumn(name = "relatorio_id", nullable = false)
    private RelatorioEntity relatorio;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity autor;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime dataCriacao;
}
