package com.branches.obra.dto.request;

import com.branches.obra.domain.enums.StatusObra;
import com.branches.obra.domain.enums.TipoContratoDeObra;
import com.branches.obra.domain.enums.TipoMaoDeObra;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.With;

import java.time.LocalDate;

@With
public record CreateObraRequest(
        @NotBlank(message = "O campo 'nome' é obrigatório")
        String nome,
        String responsavel,
        String contratante,
        @NotNull(message = "O campo 'tipoContrato' é obrigatório")
        TipoContratoDeObra tipoContrato,
        @NotNull(message = "O campo 'dataInicio' é obrigatório")
        LocalDate dataInicio,
        @NotNull(message = "O campo 'dataPrevistaFim' é obrigatório")
        LocalDate dataPrevistaFim,
        String numeroContrato,
        String endereco,
        String observacoes,
        @NotNull(message = "O campo 'tipoMaoDeObra é obrigatório") TipoMaoDeObra tipoMaoDeObra,
        @NotNull(message = "O campo 'status' é obrigatório")
        StatusObra status,
        @NotNull(message = "O campo 'modeloDeRelatorioId' é obrigatório")
        Long modeloDeRelatorioId,
        Long grupoId
) {}