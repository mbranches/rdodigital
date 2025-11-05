package com.branches.user.domain;

import com.branches.user.domain.enums.PerfilUserTenant;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PerfilUserTenant perfil;

    @PrePersist
    public void prePersist() {
        if (this.id != null) return;

        this.id = UserTenantKey.from(this.user.getId(), this.tenantId);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UserTenantEntity that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(user, that.user) && Objects.equals(tenantId, that.tenantId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, tenantId);
    }
}
