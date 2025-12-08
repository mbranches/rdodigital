package com.branches.configuradores.service;

import com.branches.configuradores.repositorio.ModeloDeRelatorioRepository;
import com.branches.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CheckIfAlreadyExistsAnotherModeloWithTheTituloService {
    private final ModeloDeRelatorioRepository modeloDeRelatorioRepository;

    public void execute(String titulo, Long tenantId) {
        boolean exists = modeloDeRelatorioRepository.existsByTituloAndTenantIdAndAtivoIsTrue(titulo, tenantId);

        if (!exists) return;

        throw new BadRequestException("Você já possui um modelo de relatório com este título");
    }

    public void executeExcludingId(String titulo, Long tenantId, Long idToExclude) {
        boolean exists = modeloDeRelatorioRepository.existsByTituloAndTenantIdAndIdIsNotAndAtivoIsTrue(titulo, tenantId, idToExclude);

        if (!exists) return;

        throw new BadRequestException("Você já possui um modelo de relatório com este título");
    }

}
