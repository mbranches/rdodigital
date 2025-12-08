package com.branches.usertenant.domain;

import com.branches.user.domain.UserEntity;
import com.branches.usertenant.domain.enums.PerfilUserTenant;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class UserTenantEntity {
    @EmbeddedId
    private UserTenantKey id;

    @MapsId("userId")
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "tenant_id", insertable = false, updatable = false)
    private Long tenantId;

    @Column(length = 100, nullable = false)
    private String cargo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PerfilUserTenant perfil;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "userTenant")
    private Set<UserObraPermitidaEntity> userObraPermitidaEntities;

    @Convert(converter = UserTenantAuthoritiesConverter.class)
    @Column(nullable = false, columnDefinition = "TEXT")
    private Authorities authorities;

    @Builder.Default
    @Column(nullable = false)
    private Boolean ativo = true;

    public List<Long> getObrasPermitidasIds() {
        return this.userObraPermitidaEntities.stream()
                .map(UserObraPermitidaEntity::getObraId)
                .toList();
    }

    @PrePersist
    public void prePersist() {
        if (id != null) return;

        setarId();
    }

    public void setarId() {
        id = UserTenantKey.from(this.user.getId(), this.tenantId);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UserTenantEntity that)) return false;
        return Objects.equals(user, that.user) && Objects.equals(tenantId, that.tenantId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, tenantId);
    }

    public boolean isAdministrador() {
        return this.perfil == PerfilUserTenant.ADMINISTRADOR;
    }
}
