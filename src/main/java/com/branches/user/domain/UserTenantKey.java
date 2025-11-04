package com.branches.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@EqualsAndHashCode
@Setter
@Getter
@Embeddable
public class UserTenantKey implements Serializable {
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    public static UserTenantKey from(Long userId, Long tenantId) {
        UserTenantKey id = new UserTenantKey();

        id.setUserId(userId);
        id.setTenantId(tenantId);

        return id;
    }
}
