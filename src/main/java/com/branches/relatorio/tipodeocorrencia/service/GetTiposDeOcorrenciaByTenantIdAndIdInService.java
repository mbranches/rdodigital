package com.branches.relatorio.tipodeocorrencia.service;

import com.branches.exception.NotFoundException;
import com.branches.relatorio.tipodeocorrencia.domain.TipoDeOcorrenciaEntity;
import com.branches.relatorio.tipodeocorrencia.repository.TipoDeOcorrenciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class GetTiposDeOcorrenciaByTenantIdAndIdInService {
    private final TipoDeOcorrenciaRepository tipoDeOcorrenciaRepository;

    public List<TipoDeOcorrenciaEntity> execute(Long tenantId, Set<Long> ids) {
        List<TipoDeOcorrenciaEntity> response = tipoDeOcorrenciaRepository.findAllByIdInAndTenantId(ids, tenantId);

        if (response.size() != ids.size()) {
            List<Long> missingIds = new ArrayList<>(ids);
            missingIds.removeAll(response.stream().map(TipoDeOcorrenciaEntity::getId).toList());

            throw new NotFoundException("Tipo(s) de ocorrencia nao encontrado(s) para o(s) id(s): " + missingIds);
        }
        return response;
    }
}
