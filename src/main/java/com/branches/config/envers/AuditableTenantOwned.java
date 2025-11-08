package com.branches.config.envers;


import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@AllArgsConstructor
@SuperBuilder
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Audited
@NoArgsConstructor
public abstract class AuditableTenantOwned {

    @CreatedDate
    @Column(name = "envers_created_date")
    private LocalDateTime enversCreatedDate;

    @CreatedBy
    @Column(name = "envers_creator")
    private Long enversCreator;

    @LastModifiedBy
    @Column(name = "envers_modifier")
    private Long enversModifier;

    @LastModifiedDate
    @Column(name = "envers_last_modified_date")
    private LocalDateTime enversLastModifiedDate;

    @Column(nullable = false)
    private Long tenantId;
}