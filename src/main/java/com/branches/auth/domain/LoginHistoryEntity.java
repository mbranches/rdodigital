package com.branches.auth.domain;

import com.branches.auth.domain.enums.LoginType;
import com.branches.config.envers.Auditable;
import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class LoginHistoryEntity extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String accessToken;

    private String addressIp;
    private String userAgent;
    private String device;
    private String browser;
    private String os;

    @Enumerated(EnumType.STRING)
    private LoginType loginType;
}
