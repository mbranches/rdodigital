package com.branches.suporte.entity;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class IntencaoDeContatoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String nome;
    @Column(nullable = false)
    private String email;

    private String telefone;

    private String empresa;

    private String mensagem;

    @Column(nullable = false)
    private Boolean enviado;
}
