package com.branches.user.service;

import com.branches.user.domain.UserEntity;
import com.branches.user.dto.request.UpdateUserInfosRequest;
import com.branches.user.repository.UserRepository;
import com.branches.utils.FullNameFormatter;
import com.branches.utils.ValidateFullName;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UpdateUserInfosService {
    private final FullNameFormatter fullNameFormatter;
    private final ValidateFullName validateFullName;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public void execute(UserEntity userEntity, UpdateUserInfosRequest request) {
        validateFullName.execute(request.nome());
        String formattedName = fullNameFormatter.execute(userEntity.getNome());
        userEntity.setNome(formattedName);

        if (request.password() != null && !request.password().isBlank()) {
            userEntity.setPassword(passwordEncoder.encode(request.password()));
        }

        userRepository.save(userEntity);
    }
}
