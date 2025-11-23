package com.branches.comentarios.service;

import com.branches.exception.ForbiddenException;
import com.branches.obra.domain.ObraEntity;
import com.branches.obra.service.GetObraByIdAndTenantIdService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CheckIfConfiguracaoDeRelatorioDaObraPermiteComentarioService {
    private final GetObraByIdAndTenantIdService getObraByIdAndTenantIdService;

    public void execute(Long obraId, Long tenantId) {
        ObraEntity obra = getObraByIdAndTenantIdService.execute(obraId, tenantId);

        if (obra.getConfiguracaoRelatorios().getShowComentarios()) return;

        throw new ForbiddenException();
    }
}
