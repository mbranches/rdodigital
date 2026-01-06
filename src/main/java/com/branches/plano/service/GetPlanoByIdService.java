package com.branches.plano.service;

import com.branches.exception.BadRequestException;
import com.branches.plano.domain.PlanoEntity;
import com.branches.plano.repository.PlanoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GetPlanoByIdService {
    private final PlanoRepository planoRepository;

    public PlanoEntity execute(Long planoId) {
        return planoRepository.findById(planoId)
                .orElseThrow(() -> new BadRequestException("Plano não encontrado"));
    }
}

