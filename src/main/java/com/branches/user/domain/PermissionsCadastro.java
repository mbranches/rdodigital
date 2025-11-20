package com.branches.user.domain;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class PermissionsCadastro {
    private Boolean grupoDeObras;
    private Boolean equipamentos;
    private Boolean maoDeObra;
    private Boolean tiposDeOcorrencia;
    private Boolean modelosDeRelatorio;

    public static PermissionsCadastro fullPermissions() {
        return new PermissionsCadastro(
                true,
                true,
                true,
                true,
                true
        );
    }
}
