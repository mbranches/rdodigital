package com.branches.user.domain;

import com.branches.user.domain.enums.Role;
import com.branches.usertenant.domain.UserTenantEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Objects;
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
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;
    @Column(columnDefinition = "TEXT")
    private String fotoUrl;
    @Builder.Default
    @Column(nullable = false)
    private Boolean ativo = true;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "user")
    private List<UserTenantEntity> userTenantEntities;

    public List<Long> getTenantsIds() {
        return userTenantEntities.stream()
                .map(UserTenantEntity::getTenantId)
                .toList();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UserEntity user)) return false;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
