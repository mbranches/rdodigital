package com.branches.user.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "user_tenant")
public class UserTenantId {
    @EmbeddedId
    private UserTenantKey id;

    @MapsId("userId")
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "tenant_id", insertable = false, updatable = false)
    private Long tenantId;

    @PrePersist
    public void prePersist() {
        if (this.id != null) return;

        this.id = UserTenantKey.from(this.user.getId(), this.tenantId);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UserTenantId that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(user, that.user) && Objects.equals(tenantId, that.tenantId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, tenantId);
    }
}
