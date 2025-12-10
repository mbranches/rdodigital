package com.branches.auth.service;

import com.branches.auth.dto.request.RegisterRequest;
import com.branches.tenant.domain.TenantEntity;
import com.branches.tenant.repository.TenantRepository;
import com.branches.tenant.service.CheckIfDoesntExistsTenantWithTheCnpjService;
import com.branches.tenant.service.CheckIfDoesntExistsTenantWithTheTelefoneService;
import com.branches.user.domain.UserEntity;
import com.branches.user.domain.enums.Role;
import com.branches.user.repository.UserRepository;
import com.branches.user.service.CheckIfDoesntExistsUserWithTheEmailService;
import com.branches.usertenant.domain.Authorities;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.domain.enums.PerfilUserTenant;
import com.branches.usertenant.repository.UserTenantRepository;
import com.branches.utils.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class RegisterService {
    private final CheckIfDoesntExistsTenantWithTheCnpjService checkIfDoesntExistsTenantWithTheCnpjService;
    private final CheckIfDoesntExistsTenantWithTheTelefoneService checkIfDoesntExistsTenantWithTheTelefoneService;
    private final CheckIfDoesntExistsUserWithTheEmailService checkIfDoesntExistsUserWithTheEmailService;
    private final ValidateFullName validateFullName;
    private final ValidatePhoneNumber validatePhoneNumber;
    private final ValidatePassword validatePassword;
    private final FullNameFormatter fullNameFormatter;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final UserTenantRepository userTenantRepository;
    private final ValidateCnpj validateCnpj;

    @Transactional
    public void execute(RegisterRequest request) {
        validateFields(request);

        String responsavelFormattedName = fullNameFormatter.execute(request.responsavelNome());

        UserEntity userToSave = UserEntity.builder()
                .nome(responsavelFormattedName)
                .email(request.responsavelEmail().trim().toLowerCase())
                .password(passwordEncoder.encode(request.responsavelPassword()))
                .role(Role.USER)
                .build();

        UserEntity userResponsavel = userRepository.save(userToSave);

        TenantEntity tenantToSave = TenantEntity.builder()
                .razaoSocial(request.razaoSocial())
                .nome(request.nome())
                .cnpj(request.cnpj().replaceAll("[^0-9]", ""))
                .telefone(request.telefone())
                .segmento(request.segmento())
                .userResponsavelId(userResponsavel.getId())
                .build();

        TenantEntity savedTenant = tenantRepository.save(tenantToSave);

        UserTenantEntity userTenantToSave = UserTenantEntity.builder()
                .user(userResponsavel)
                .tenantId(savedTenant.getId())
                .cargo(request.responsavelCargo())
                .authorities(Authorities.adminAuthorities())
                .perfil(PerfilUserTenant.ADMINISTRADOR)
                .build();
        userTenantToSave.setarId();

        userTenantRepository.save(userTenantToSave);

        sendWelcomeEmail(userResponsavel.getEmail(), responsavelFormattedName, request.nome());
    }

    private void sendWelcomeEmail(String email, String responsavelFormattedName, String tenantNome) {
        // todo: implementar Lógica para enviar o email de boas-vindas
    }

    private void validateFields(RegisterRequest request) {
        validateFullName.execute(request.responsavelNome());
        validatePhoneNumber.execute(request.telefone());
        validatePassword.execute(request.responsavelPassword());
        validateCnpj.execute(request.cnpj());

        checkIfDoesntExistsTenantWithTheCnpjService.execute(request.cnpj());
        checkIfDoesntExistsTenantWithTheTelefoneService.execute(request.telefone());
        checkIfDoesntExistsUserWithTheEmailService.execute(request.responsavelEmail());
    }
}
