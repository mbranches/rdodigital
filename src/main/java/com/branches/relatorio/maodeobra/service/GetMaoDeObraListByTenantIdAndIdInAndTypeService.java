package com.branches.relatorio.maodeobra.service;

import com.branches.exception.NotFoundException;
import com.branches.obra.domain.enums.TipoMaoDeObra;
import com.branches.relatorio.maodeobra.domain.MaoDeObraEntity;
import com.branches.relatorio.maodeobra.repository.MaoDeObraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class GetMaoDeObraListByTenantIdAndIdInAndTypeService {
    private final MaoDeObraRepository maoDeObraRepository;

    public List<MaoDeObraEntity> execute(Long tenantId, Set<Long> ids, TipoMaoDeObra type) {
        List<MaoDeObraEntity> response = maoDeObraRepository.findAllByIdInAndTenantIdAndTipoAndAtivoIsTrue(ids, tenantId, type);

        if (response.size() != ids.size()) {
            List<Long> missingIds = new ArrayList<>(ids);
            missingIds.removeAll(response.stream().map(MaoDeObraEntity::getId).toList());

            throw new NotFoundException("Mão de obra não encontrada(s) com o(s) id(s): " + missingIds);
        }

        return response;
    }
}
