package com.branches.plano.service;

import com.branches.plano.domain.PlanoEntity;
import com.branches.plano.dto.response.PlanoResponse;
import com.branches.plano.repository.PlanoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ListPlanosService {
    private final PlanoRepository planoRepository;

    public List<PlanoResponse> execute() {
        List<PlanoEntity> planos = planoRepository.findAllByAtivoIsTrueOrderByValorAsc();

        return planos.stream()
                .map(PlanoResponse::from)
                .toList();
    }
}
