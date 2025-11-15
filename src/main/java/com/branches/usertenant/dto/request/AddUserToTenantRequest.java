package com.branches.usertenant.dto.request;

import com.branches.usertenant.domain.Authorities;
import com.branches.usertenant.domain.enums.PerfilUserTenant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record AddUserToTenantRequest(
        @NotBlank(message = "O campo 'userId' é obrigatório") String userId,
        @NotNull(message = "O campo 'obrasIds' é obrigatório") Set<String> obrasIds,
        Authorities authorities,
        @NotNull(message = "O campo 'perfil' é obrigatório") PerfilUserTenant perfil
) {
}
