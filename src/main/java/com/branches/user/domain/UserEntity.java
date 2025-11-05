package com.branches.user.domain;

import com.branches.user.domain.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Setter
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
    private Set<UserTenantEntity> userTenantEntities;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "user")
    private Set<UserObraPermitidaEntity> userObraPermitidaEntities;

    public List<Long> getTenantsIds() {
        return userTenantEntities.stream()
                .map(UserTenantEntity::getTenantId)
                .toList();
    }

    public List<Long> getObrasPermitidasIds() {
        return userObraPermitidaEntities.stream()
                .map(UserObraPermitidaEntity::getObraId)
                .toList();
    }
}
