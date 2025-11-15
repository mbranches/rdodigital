package com.branches.usertenant.domain;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class PermissionsItensDeRelatorio {
    private Boolean ocorrencias;
    private Boolean atividades;
    private Boolean equipamentos;
    private Boolean maoDeObra;
    private Boolean comentarios;
    private Boolean fotos;
    private Boolean condicaoDoClima;

    public static PermissionsItensDeRelatorio fullPermissions() {
        return new PermissionsItensDeRelatorio(
                true,
                true,
                true,
                true,
                true,
                true,
                true
        );
    }
}
