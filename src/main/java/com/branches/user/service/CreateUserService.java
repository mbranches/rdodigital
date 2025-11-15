package com.branches.user.service;

import com.branches.exception.BadRequestException;
import com.branches.user.domain.UserEntity;
import com.branches.user.domain.enums.Role;
import com.branches.user.dto.request.CreateUserRequest;
import com.branches.user.dto.response.CreateUserResponse;
import com.branches.user.repository.UserRepository;
import com.branches.utils.FullNameFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CreateUserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final FullNameFormatter fullNameFormatter;

    public CreateUserResponse execute(CreateUserRequest request, Boolean requestingUserIsAdmin) {
        if (request.nome().split(" ").length < 2) {
            throw new BadRequestException("Informe o nome completo do usuário.");
        }

        validatePassword(request.password());

        String formattedEmail = request.email().trim().toLowerCase();

        if (userRepository.existsByEmail(formattedEmail)) {
            throw new BadRequestException("Já existe um usuário cadastrado com o e-mail informado.");
        }

        String formattedNome = fullNameFormatter.execute(request.nome());
        UserEntity userToSave = UserEntity.builder()
                .nome(formattedNome)
                .email(formattedEmail)
                .password(passwordEncoder.encode(request.password()))
                .cargo(request.cargo())
                .role(requestingUserIsAdmin ? request.role() : Role.USER)
                .ativo(true)
                .build();

        UserEntity saved = userRepository.save(userToSave);

        return CreateUserResponse.from(saved);
    }

    private void validatePassword(String password) {
        if (password.length() < 6) {
            throw new BadRequestException("A senha deve ter no mínimo 6 caracteres.");
        }

        String passwordPattern = "^[a-zA-Z0-9_]+$";
        if (!password.matches(passwordPattern)) {
            throw new BadRequestException("A senha pode conter somente letras, números e o caractere '_'");
        }
    }
}
