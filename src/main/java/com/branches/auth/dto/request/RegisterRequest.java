package com.branches.auth.dto.request;

import com.branches.tenant.domain.enums.SegmentoTenant;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.br.CNPJ;

public record RegisterRequest(
        @NotBlank(message = "O campo 'razaoSocial' é obrigatório")
        String razaoSocial,
        @NotBlank(message = "O campo 'nome' é obrigatório")
        String nome,
        @NotBlank(message = "O campo 'cnpj' é obrigatório")
        String cnpj,
        @NotBlank(message = "O campo 'telefone' é obrigatório")
        String telefone,
        @NotBlank(message = "O campo 'responsavelNome' é obrigatório")
        String responsavelNome,
        @NotBlank(message = "O campo 'responsavelCargo' é obrigatório")
        String responsavelCargo,
        @NotBlank(message = "O campo 'responsavelEmail' é obrigatório")
        @Email(message = "O campo 'responsavelEmail' deve ser um email válido")
        String responsavelEmail,
        @NotBlank(message = "O campo 'responsavelPassword' é obrigatório")
        String responsavelPassword,
        @NotNull(message = "O campo 'segmento' é obrigatório")
        SegmentoTenant segmento
) {
}
