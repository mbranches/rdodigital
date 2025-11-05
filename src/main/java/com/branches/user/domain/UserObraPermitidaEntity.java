package com.branches.user.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class UserObraPermitidaEntity {
    @EmbeddedId
    private UserObraPermitidaKey id;

    @MapsId("userId")
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "obra_id", nullable = false, insertable = false, updatable = false)
    private Long obraId;

    @PrePersist
    public void prePersist() {
        if (id != null) return;

        this.id = UserObraPermitidaKey.from(this.user.getId(), this.obraId);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UserObraPermitidaEntity that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(user, that.user) && Objects.equals(obraId, that.obraId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, obraId);
    }
}
