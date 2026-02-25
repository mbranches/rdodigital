package com.branches.auth.service;

import com.branches.auth.dto.request.RegisterRequest;
import com.branches.configuradores.domain.ModeloDeRelatorioEntity;
import com.branches.configuradores.domain.enums.RecorrenciaRelatorio;
import com.branches.configuradores.repositorio.ModeloDeRelatorioRepository;
import com.branches.shared.email.EmailSender;
import com.branches.shared.email.SendEmailRequest;
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
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class RegisterService {
    private static final String LINK_SISTEMA = "https://app.rdodigital.com.br";

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
    private final ModeloDeRelatorioRepository modeloDeRelatorioRepository;
    private final EmailSender emailSender;
    private final TemplateEngine templateEngine;

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

        createModeloDeRelatorioDefault(savedTenant.getId());

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

    private void createModeloDeRelatorioDefault(Long id) {
        ModeloDeRelatorioEntity modeloDeRelatorio = ModeloDeRelatorioEntity.builder()
                .titulo("Relatório Diário de Obra (RDO)")
                .recorrenciaRelatorio(RecorrenciaRelatorio.UM_POR_DIA)
                .showCondicaoClimatica(true)
                .showMaoDeObra(true)
                .showEquipamentos(true)
                .showAtividades(true)
                .showOcorrencias(true)
                .showComentarios(true)
                .showHorarioDeTrabalho(true)
                .showFotos(true)
                .showVideos(true)
                .showMateriais(true)
                .isDefault(true)
                .tenantId(id)
                .build();

        modeloDeRelatorioRepository.save(modeloDeRelatorio);
    }

    private void sendWelcomeEmail(String email, String responsavelFormattedName, String tenantNome) {
        Map<String, Object> variables = Map.ofEntries(
                Map.entry("userName", responsavelFormattedName),
                Map.entry("tenantName", tenantNome),
                Map.entry("linkToAccess", LINK_SISTEMA)
        );

        Context context = new Context();
        context.setVariables(variables);

        String html = templateEngine.process("email-welcome-register", context);

        System.out.println(html);
        SendEmailRequest emailRequest = SendEmailRequest.builder()
                .to(email)
                .subject("Bem-vindo ao RDO Digital")
                .body(html)
                .build();

        emailSender.sendEmail(emailRequest, true);
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
