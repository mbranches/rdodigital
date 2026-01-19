package com.branches.usertenant.service;

import com.branches.assinaturadeplano.domain.AssinaturaDePlanoEntity;
import com.branches.assinaturadeplano.service.GetAssinaturaActiveByTenantIdService;
import com.branches.exception.BadRequestException;
import com.branches.exception.ForbiddenException;
import com.branches.obra.domain.ObraEntity;
import com.branches.obra.service.GetObrasByTenantIdAndIdExternoIn;
import com.branches.plano.domain.PeriodoTesteEntity;
import com.branches.plano.service.FindTenantPeriodoTesteService;
import com.branches.shared.email.EmailSender;
import com.branches.shared.email.SendEmailRequest;
import com.branches.tenant.domain.TenantEntity;
import com.branches.tenant.service.GetTenantByIdExternoService;
import com.branches.user.domain.UserEntity;
import com.branches.user.domain.enums.Role;
import com.branches.user.repository.UserRepository;
import com.branches.user.service.FindUserByEmailService;
import com.branches.usertenant.domain.Authorities;
import com.branches.usertenant.domain.UserObraPermitidaEntity;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.domain.UserTenantKey;
import com.branches.usertenant.domain.enums.PerfilUserTenant;
import com.branches.usertenant.dto.request.AddUserToTenantRequest;
import com.branches.usertenant.repository.UserTenantRepository;
import com.branches.utils.FullNameFormatter;
import com.branches.utils.ValidateFullName;
import com.branches.utils.ValidatePassword;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.branches.usertenant.domain.enums.PerfilUserTenant.ADMINISTRADOR;

@Transactional
@RequiredArgsConstructor
@Service
public class AddUserToTenantService {
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final GetAssinaturaActiveByTenantIdService getAssinaturaActiveByTenantIdService;
    private final UserTenantRepository userTenantRepository;
    private final GetObrasByTenantIdAndIdExternoIn getObrasByTenantIdAndIdExternoIn;
    private final FindUserByEmailService findUserByEmailService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final GetTenantByIdExternoService getTenantByIdExternoService;
    private final ValidatePassword validatePassword;
    private final FullNameFormatter fullNameFormatter;
    private final ValidateFullName validateFullName;
    private final FindTenantPeriodoTesteService findTenantPeriodoTesteService;
    private final EmailSender emailSender;
    private final SpringTemplateEngine templateEngine;
    private static final String LINK_SISTEMA = "https://app.rdodigital.com.br";

    public void execute(AddUserToTenantRequest request, String tenantExternalId, List<UserTenantEntity> userTenants) {
        TenantEntity tenant = getTenantByIdExternoService.execute(tenantExternalId);
        Long tenantId = tenant.getId();

        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        checkIfAddUserIsAllowed(currentUserTenant);

        PerfilUserTenant perfil = request.perfil();

        boolean isAdminPerfil = perfil.equals(ADMINISTRADOR);

        if (!isAdminPerfil && request.authorities() == null) {
            throw new BadRequestException("As authorities devem ser fornecidas para perfis diferentes de ADMINISTRADOR.");
        }

        Optional<UserEntity> userByEmailOptional = findUserByEmailService.execute(request.email());

        UserEntity user = userByEmailOptional
                        .orElseGet(() -> saveNewUser(request));

        checkIfUserAlreadyInTenant(user.getId(), tenantId);

        UserTenantEntity newUserTenant = UserTenantEntity.builder()
                .user(user)
                .tenantId(tenantId)
                .perfil(perfil)
                .cargo(request.cargo())
                .authorities(isAdminPerfil ? Authorities.adminAuthorities() : request.authorities())
                .ativo(true)
                .build();

        List<ObraEntity> obras = getObrasByTenantIdAndIdExternoIn.execute(tenantId, request.obrasPermitidasIds());
        Set<UserObraPermitidaEntity> userObrasPermitidaEntities = getUserObrasPermitidaEntities(newUserTenant, obras);

        newUserTenant.setUserObraPermitidaEntities(userObrasPermitidaEntities);
        newUserTenant.setarId();

        userTenantRepository.save(newUserTenant);

        boolean userIsNewInSystem = userByEmailOptional.isEmpty();
        if (userIsNewInSystem) {
            sendEmailToNewUserAddedToTenant(user.getEmail(), tenant, request.password());

            return;
        }

        sendEmailToExistingUserAddedToTenant(user.getEmail(), tenant);
    }

    private void sendEmailToNewUserAddedToTenant(String email, TenantEntity tenant, String password) {
        String subject = "Bem-vindo ao sistema RDO Digital";

        Map<String, Object> variables = Map.ofEntries(
                Map.entry("tenantNome", tenant.getNome()),
                Map.entry("email", email),
                Map.entry("password", password),
                Map.entry("linkToAccess", LINK_SISTEMA)
        );

        String html = buildHtml("email-new-user-added.html", variables);

        enviarEmail(email, subject, html);
    }

    private String buildHtml(String templateName, Map<String, Object> variables) {
        Context context = new Context();
        context.setVariables(variables);

        return templateEngine.process(templateName, context);
    }

    private void sendEmailToExistingUserAddedToTenant(String email, TenantEntity tenant) {
        String subject = "Você foi adicionado a uma nova empresa no sistema RDO Digital";

        Map<String, Object> variables = Map.ofEntries(
                Map.entry("tenantNome", tenant.getNome()),
                Map.entry("linkToAccess", LINK_SISTEMA)
        );

        String html = buildHtml("email-existing-user-added.html", variables);

        enviarEmail(email, subject, html);
    }

    private void enviarEmail(String email, String subject, String html) {
        SendEmailRequest emailRequest = SendEmailRequest.builder()
                .to(email)
                .subject(subject)
                .body(html)
                .build();

        emailSender.sendEmail(emailRequest, true);
    }

    private UserEntity saveNewUser(AddUserToTenantRequest request) {
        checkRequiredFieldsForNewUser(request);

        validatePassword.execute(request.password());
        validateFullName.execute(request.nome());

        String formattedEmail = request.email().trim().toLowerCase();
        String formattedNome = fullNameFormatter.execute(request.nome());

        UserEntity user = UserEntity.builder()
                .email(formattedEmail)
                .nome(formattedNome)
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .build();

        return userRepository.save(user);
    }

    private void checkRequiredFieldsForNewUser(AddUserToTenantRequest request) {
        StringBuilder errorMessage = new StringBuilder();

        if (request.nome() == null || request.nome().isBlank()) {
            errorMessage.append("O campo 'nome' é obrigatório para novos usuários; ");
        }

        if (request.cargo() == null || request.cargo().isBlank()) {
            errorMessage.append("O campo 'cargo' é obrigatório para novos usuários; ");
        }

        if (request.password() == null || request.password().isBlank()) {
            throw new BadRequestException("O campo 'password' é obrigatório para novos usuários");
        }

        if(errorMessage.isEmpty()) return;

        throw new BadRequestException(errorMessage.toString().trim());
    }

    private void checkIfUserAlreadyInTenant(Long userId, Long tenantId) {
        boolean userAlreadyInTenant = userTenantRepository.existsById(UserTenantKey.from(userId, tenantId));

        if (!userAlreadyInTenant) return;

        throw new BadRequestException("Usuário já pertence a este tenant");
    }

    private Set<UserObraPermitidaEntity> getUserObrasPermitidaEntities(UserTenantEntity newUserTenant, List<ObraEntity> obras) {
        List<Long> obrasIds = obras.stream().map(ObraEntity::getId).toList();

        return obrasIds.stream()
                .map(obraId -> {
                    UserObraPermitidaEntity obraPermitida = UserObraPermitidaEntity.builder()
                            .userTenant(newUserTenant)
                            .obraId(obraId)
                            .build();

                    obraPermitida.setarId();

                    return obraPermitida;
                })
                .collect(Collectors.toSet());
    }

    private void checkIfAddUserIsAllowed(UserTenantEntity currentUserTenant) {
        if (!currentUserTenant.getPerfil().equals(ADMINISTRADOR)) throw new ForbiddenException();

        Optional<PeriodoTesteEntity> optionalPeriodoTeste = findTenantPeriodoTesteService.execute(currentUserTenant.getTenantId());

        if (optionalPeriodoTeste.isPresent() && optionalPeriodoTeste.get().isInProgress()) {
            return;
        }

        long quantityOfUsers = userTenantRepository.countByTenantId(currentUserTenant.getTenantId());

        AssinaturaDePlanoEntity activeAssinatura = getAssinaturaActiveByTenantIdService.execute(currentUserTenant.getTenantId());

        if (quantityOfUsers >= activeAssinatura.getPlano().getLimiteUsuarios()) {
            throw new BadRequestException("Limite de usuários atingido para o plano atual.");
        }
    }
}
