package com.branches.usertenant.dto.response;

import com.branches.obra.dto.response.ObraPermitidaResponse;
import com.branches.obra.repository.projections.ObraResumeProjection;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.domain.enums.PerfilUserTenant;

import java.util.List;

public record UserTenantResponse(
        String userExternalId,
        String nome,
        String email,
        String cargo,
        String fotoUrl,
        Boolean ativo,
        PerfilUserTenant perfil,
        List<ObraPermitidaResponse> obrasPermitidas
) {
    public static UserTenantResponse from(UserTenantEntity userTenant, List<ObraResumeProjection> obrasPermitidas) {
        List<ObraPermitidaResponse> obraPermitidaResponse = obrasPermitidas.stream()
                .map(ObraPermitidaResponse::from)
                .toList();

        return new UserTenantResponse(
                userTenant.getUser().getIdExterno(),
                userTenant.getUser().getNome(),
                userTenant.getUser().getEmail(),
                userTenant.getCargo(),
                userTenant.getUser().getFotoUrl(),
                userTenant.getUser().getAtivo(),
                userTenant.getPerfil(),
                obraPermitidaResponse
        );
    }
}
