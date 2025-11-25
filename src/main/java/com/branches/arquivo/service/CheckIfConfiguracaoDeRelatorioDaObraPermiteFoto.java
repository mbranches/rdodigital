package com.branches.arquivo.service;

import com.branches.exception.ForbiddenException;
import com.branches.obra.domain.ObraEntity;
import com.branches.relatorio.repository.projections.RelatorioWithObraProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CheckIfConfiguracaoDeRelatorioDaObraPermiteFoto {

    public void execute(RelatorioWithObraProjection relatorio) {
        ObraEntity obra = relatorio.getObra();

        if (obra.getConfiguracaoRelatorios().getShowFotos()) return;

        throw new ForbiddenException();
    }
}
