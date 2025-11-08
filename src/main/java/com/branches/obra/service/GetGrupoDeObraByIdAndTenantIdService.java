package com.branches.obra.service;

import com.branches.obra.domain.GrupoDeObraEntity;
import com.branches.exception.NotFoundException;
import com.branches.obra.repository.GrupoDeObraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GetGrupoDeObraByIdAndTenantIdService {
    private final GrupoDeObraRepository grupoDeObraRepository;

    public GrupoDeObraEntity execute(Long id, Long tenantId) {
        return grupoDeObraRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new NotFoundException("Grupo de Obra não encontrado com id: " + id + " e tenantId: " + tenantId));
    }
}
