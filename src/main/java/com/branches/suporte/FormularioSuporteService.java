package com.branches.suporte;

import com.branches.shared.email.EmailSender;
import com.branches.shared.email.SendEmailRequest;
import com.branches.shared.email.SendEmailResponse;
import com.branches.suporte.dto.request.FormularioSuporteRequest;
import com.branches.suporte.entity.TicketDeSuporteEntity;
import com.branches.suporte.repository.TicketDeSuporteRepository;
import com.branches.tenant.domain.TenantEntity;
import com.branches.tenant.service.GetTenantByIdExternoService;
import com.branches.user.domain.UserEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FormularioSuporteService {
    private final GetTenantByIdExternoService getTenantByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final EmailSender emailSender;
    private static final String EMAIL_DESTINO = "marcus.branches@icloud.com";
    private final TicketDeSuporteRepository ticketDeSuporteRepository;

    public void execute(String tenantExternalId, UserEntity requestingUser, FormularioSuporteRequest request) {
        TenantEntity tenant = getTenantByIdExternoService.execute(tenantExternalId);

        Long tenantId = tenant.getId();
        getCurrentUserTenantService.execute(requestingUser.getUserTenantEntities(), tenantId);

        TicketDeSuporteEntity ticketDeSuporte = TicketDeSuporteEntity.builder()
                .user(requestingUser)
                .tipoSuporte(request.tipoSuporte())
                .assunto(request.assunto())
                .descricao(request.descricao())
                .tenantId(tenantId)
                .build();

        String emailContent = String.format(
                """
                        Nova intenção de suporte recebida:
                        Usuário: %s
                        Tipo de Suporte: %s
                        Assunto: %s
                        Descrição: %s
                """,
                requestingUser.getNome(),
                request.tipoSuporte().getDescricao(),
                request.assunto(),
                request.descricao()
        );

        SendEmailResponse sendEmailResponse = emailSender.sendEmail(
                SendEmailRequest.builder().to(EMAIL_DESTINO).subject("Novo ticket de suporte aberto").body(emailContent).build(),
                false
        );

        ticketDeSuporte.setEnviado(sendEmailResponse.wasSentSuccessfully());

        ticketDeSuporteRepository.save(ticketDeSuporte);

    }
}
