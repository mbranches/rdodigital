package com.branches.ocorrencia.service;

import com.branches.exception.NotFoundException;
import com.branches.ocorrencia.domain.TipoDeOcorrenciaEntity;
import com.branches.ocorrencia.repository.TipoDeOcorrenciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GetTipoDeOcorrenciaByIdAndTenantIdService {
    private final TipoDeOcorrenciaRepository tipoDeOcorrenciaRepository;

    public TipoDeOcorrenciaEntity execute(Long id, Long tenantId) {
        return tipoDeOcorrenciaRepository.findByIdAndTenantIdAndAtivoIsTrue(id, tenantId)
                .orElseThrow(() -> new NotFoundException("Tipo de ocorrência não encontrado com o id: " + id));
    }
}
