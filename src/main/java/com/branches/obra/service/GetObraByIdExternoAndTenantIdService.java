package com.branches.obra.service;

import com.branches.exception.NotFoundException;
import com.branches.obra.domain.ObraEntity;
import com.branches.obra.repository.ObraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GetObraByIdExternoAndTenantIdService {
    private final ObraRepository obraRepository;

    public ObraEntity execute(String idExterno, Long tenantId) {
        return obraRepository.findByIdExternoAndTenantIdAndAtivoIsTrue(idExterno, tenantId)
                .orElseThrow(() -> new NotFoundException("Obra com idExterno " + idExterno + " não encontrada para o tenant informado"));
    }
}
