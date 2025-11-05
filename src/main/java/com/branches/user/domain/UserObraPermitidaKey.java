package com.branches.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode
@Setter
@Getter
@Embeddable
public class UserObraPermitidaKey {
    @Column(nullable = false)
    private Long userId;
    @Column(nullable = false )
    private Long obraId;

    public static UserObraPermitidaKey from(Long userId, Long obraId) {
        UserObraPermitidaKey key = new UserObraPermitidaKey();

        key.setUserId(userId);
        key.setObraId(obraId);

        return key;
    }
}
