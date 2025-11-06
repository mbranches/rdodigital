package com.branches.user.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserTenantAuthorities {
    private PermissionsRelatorio relatorios;
    private PermissionsDefault obras;
    private PermissionsCadastro cadastros;
}
