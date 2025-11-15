package com.branches.usertenant.domain;

import com.branches.user.domain.PermissionsCadastro;
import com.branches.user.domain.PermissionsDefault;
import com.branches.user.domain.PermissionsRelatorio;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Authorities {
    private PermissionsRelatorio relatorios;
    private PermissionsDefault obras;
    private PermissionsCadastro cadastros;
    private PermissionsItensDeRelatorio itensDeRelatorio;

    public static Authorities adminAuthorities() {
        return Authorities.builder()
                .relatorios(PermissionsRelatorio.fullPermissions())
                .obras(PermissionsDefault.fullPermissions())
                .cadastros(PermissionsCadastro.fullPermissions())
                .itensDeRelatorio(PermissionsItensDeRelatorio.fullPermissions())
                .build();
    }
}
