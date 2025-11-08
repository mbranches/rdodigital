package com.branches.relatorio.rdo.domain;
import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class CampoPersonalizadoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100)
    private String campo;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String descricao;
}
