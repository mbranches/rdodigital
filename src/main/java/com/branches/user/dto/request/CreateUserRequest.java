package com.branches.user.dto.request;

import com.branches.user.domain.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank(message = "O campo 'nome' é obrigatório")
        String nome,
        @NotBlank(message = "O campo 'email' é obrigatório")
        @Email(message = "O campo 'email' deve ser um e-mail válido")
        String email,
        @NotBlank(message = "O campo 'password' é obrigatório")
        @Size(max = 72, message = "O campo 'password' deve ter no máximo 72 caracteres")
        String password,
        @NotBlank(message = "O campo 'cargo' é obrigatório")
        String cargo,
        Role role
) {
}
