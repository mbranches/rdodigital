package com.branches.maodeobra.service;

import com.branches.exception.NotFoundException;
import com.branches.obra.domain.enums.TipoMaoDeObra;
import com.branches.maodeobra.domain.MaoDeObraEntity;
import com.branches.maodeobra.repository.MaoDeObraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GetMaoDeObraByIdAndTenantIdAndTypeService {
    private final MaoDeObraRepository maoDeObraRepository;

    public MaoDeObraEntity execute(Long id, Long tenantId, TipoMaoDeObra type) {
        return maoDeObraRepository.findByIdAndTenantIdAndTipoAndAtivoIsTrue(id, tenantId, type)
            .orElseThrow(() -> new NotFoundException("Mão de obra não encontrada com o id: %d".formatted(id)));
    }
}
