package com.branches.user.service;

import com.branches.exception.NotFoundException;
import com.branches.user.domain.UserEntity;
import com.branches.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GetUserByIdExternoService {
    private final UserRepository userRepository;

    public UserEntity execute(String idExterno) {
        return userRepository.findByIdExternoAndAtivoIsTrue(idExterno)
                .orElseThrow(() -> new NotFoundException("User não encontrado com idExterno: " + idExterno));
    }
}
