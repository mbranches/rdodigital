package com.branches.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@EqualsAndHashCode
@Setter
@Getter
@Embeddable
public class UserObraPermitidaKey implements Serializable {
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Column(name = "obra_id", nullable = false)
    private Long obraId;
    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    public static UserObraPermitidaKey from(UserTenantEntity userTenant, Long obraId) {
        UserObraPermitidaKey key = new UserObraPermitidaKey();

        key.setUserId(userTenant.getUser().getId());
        key.setObraId(obraId);
        key.setTenantId(userTenant.getTenantId());

        return key;
    }
}
