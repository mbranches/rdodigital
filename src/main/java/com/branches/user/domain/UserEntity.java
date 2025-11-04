package com.branches.user.domain;

import com.branches.user.domain.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Builder.Default
    @Column(unique = true, nullable = false)
    private String idExterno = UUID.randomUUID().toString();
    @Column(length = 100, nullable = false)
    private String nome;
    @Column(length = 100, unique = true, nullable = false)
    private String email;
    @Column(length = 100, nullable = false)
    private String password;
    @Column(length = 100, nullable = false)
    private String cargo;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;
    @Column(columnDefinition = "TEXT")
    private String fotoUrl;
    @Column(nullable = false)
    private Boolean ativo;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "user")
    private Set<UserTenantId> userTenantIds;
}
