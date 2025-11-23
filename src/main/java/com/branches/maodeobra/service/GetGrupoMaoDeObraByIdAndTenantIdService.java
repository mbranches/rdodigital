
package com.branches.maodeobra.service;

import com.branches.maodeobra.domain.GrupoMaoDeObraEntity;
import com.branches.exception.NotFoundException;
import com.branches.maodeobra.repository.GrupoMaoDeObraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GetGrupoMaoDeObraByIdAndTenantIdService {
    private final GrupoMaoDeObraRepository grupoMaoDeObraRepository;

    public GrupoMaoDeObraEntity execute(Long id, Long tenantId) {
        return grupoMaoDeObraRepository.findByIdAndTenantIdAndAtivoIsTrue(id, tenantId)
                .orElseThrow(() -> new NotFoundException("Grupo de Mão de Obra não encontrado com id: " + id + " e tenantId: " + tenantId));
    }
}
