package com.branches.auth.service;

import com.branches.assinatura.domain.AssinaturaEntity;
import com.branches.assinatura.domain.enums.AssinaturaStatus;
import com.branches.assinatura.repository.AssinaturaRepository;
import com.branches.auth.dto.request.RegisterRequest;
import com.branches.configuradores.domain.ModeloDeRelatorioEntity;
import com.branches.configuradores.domain.enums.RecorrenciaRelatorio;
import com.branches.configuradores.repositorio.ModeloDeRelatorioRepository;
import com.branches.plano.repository.PlanoRepository;
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

import java.time.LocalDate;

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
    private final ModeloDeRelatorioRepository modeloDeRelatorioRepository;
    private final PlanoRepository planoRepository;
    private final AssinaturaRepository assinaturaRepository;

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

        planoRepository.findByNome("Plano Gratuito").ifPresent(plano -> {
            LocalDate dataFim = switch (plano.getRecorrencia()) {
                case DIARIO -> LocalDate.now().plusDays(1);
                case SEMANAL -> LocalDate.now().plusWeeks(1);
                case MENSAL -> LocalDate.now().plusMonths(1);
                case ANUAL -> LocalDate.now().plusYears(1);
            };

            AssinaturaEntity assinatura = AssinaturaEntity.builder()
                    .status(AssinaturaStatus.ATIVO)
                    .plano(plano)
                    .tenantId(savedTenant.getId())
                    .dataInicio(LocalDate.now())
                    .dataFim(dataFim)
                    .build();

            assinaturaRepository.save(assinatura);
        });

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
