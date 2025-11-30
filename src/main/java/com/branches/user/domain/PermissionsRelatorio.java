package com.branches.user.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PermissionsRelatorio extends PermissionsDefault {
    private Boolean canAprovar;
    private Boolean canViewOnlyAprovados;
    private Boolean canAddFotos;
    private Boolean canAddComentarios;

    public PermissionsRelatorio(Boolean canCreateAndEdit, Boolean canDelete, Boolean canAprovar, Boolean canViewOnlyAprovados, Boolean canAddFotos, Boolean canAddComentarios) {
        super(canCreateAndEdit, canDelete);
        this.canAprovar = canAprovar;
        this.canViewOnlyAprovados = canViewOnlyAprovados;
        this.canAddFotos = canAddFotos;
        this.canAddComentarios = canAddComentarios;
    }

    public static PermissionsRelatorio fullPermissions() {
        return new PermissionsRelatorio(
                true,
                true,
                true,
                false,
                true,
                true
        );
    }
}
