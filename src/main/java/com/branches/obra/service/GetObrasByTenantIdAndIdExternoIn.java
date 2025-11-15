package com.branches.obra.service;

import com.branches.exception.NotFoundException;
import com.branches.obra.domain.ObraEntity;
import com.branches.obra.repository.ObraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class GetObrasByTenantIdAndIdExternoIn {
    private final ObraRepository obraRepository;

    public List<ObraEntity> execute(Long tenantId, Collection<String> obrasExternalIds) {
        List<ObraEntity> obrasList = obraRepository.findAllByIdExternoInAndTenantIdAndAtivoIsTrue(obrasExternalIds, tenantId);

        if (obrasList.size() != obrasExternalIds.size()) {
            Set<String> foundIds = obrasList.stream().map(ObraEntity::getIdExterno).collect(Collectors.toSet());
            obrasExternalIds.removeAll(foundIds);

            throw new NotFoundException("Obra(s) não encontrada(s) para o(s) id(s): " + obrasExternalIds);
        }

        return obrasList;
    }
}
