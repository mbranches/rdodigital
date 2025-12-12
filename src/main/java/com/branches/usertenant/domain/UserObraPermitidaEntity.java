package com.branches.usertenant.domain;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class UserObraPermitidaEntity {
    @EmbeddedId
    private UserObraPermitidaKey id;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false),
            @JoinColumn(name = "tenant_id", referencedColumnName = "tenant_id", insertable = false, updatable = false)
    })
    private UserTenantEntity userTenant;

    @Column(name = "obra_id", nullable = false, insertable = false, updatable = false)
    private Long obraId;

    public void setarId() {
        this.id = UserObraPermitidaKey.from(userTenant, obraId);
    }
}
