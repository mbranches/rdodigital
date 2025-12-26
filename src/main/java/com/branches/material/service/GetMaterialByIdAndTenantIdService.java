package com.branches.material.service;

import com.branches.exception.NotFoundException;
import com.branches.material.domain.MaterialEntity;
import com.branches.material.repository.MaterialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GetMaterialByIdAndTenantIdService {
    private final MaterialRepository materialRepository;

    public MaterialEntity execute(Long materialId, Long tenantId) {
        return materialRepository.findByIdAndTenantIdAndAtivoIsTrue(materialId, tenantId)
                .orElseThrow(() -> new NotFoundException("Material não encontrado id: " + materialId ));
    }
}
