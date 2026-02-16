package com.branches.suporte.controller;

import com.branches.shared.email.SendEmailResponse;
import com.branches.suporte.dto.request.FormularioContatoRequest;
import com.branches.suporte.entity.IntencaoDeContatoEntity;
import com.branches.suporte.repository.IntencaoDeContatoRepository;
import com.branches.shared.email.EmailSender;
import com.branches.shared.email.SendEmailRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Contato")
@RequiredArgsConstructor
@RestController
public class FormularioContatoController {

    private final IntencaoDeContatoRepository intencaoDeContatoRepository;
    private final EmailSender emailSender;
    private static final String EMAIL_DESTINO = "marcus.branches@icloud.com";

    @PostMapping("/api/formulario-contato")
    public ResponseEntity<Void> execute(@RequestBody @Valid FormularioContatoRequest request) {
        IntencaoDeContatoEntity contato = IntencaoDeContatoEntity.builder()
                .nome(request.nome())
                .email(request.email())
                .telefone(request.telefone())
                .empresa(request.Empresa())
                .mensagem(request.mensagem())
                .build();

        SendEmailRequest emailRequest = SendEmailRequest.builder()
                .to(EMAIL_DESTINO)
                .subject("Nova Intenção de Contato: " + request.nome())
                .body("Você recebeu uma nova intenção de contato.\n\n" +
                        "Nome: " + request.nome() + "\n" +
                        "Email: " + request.email() + "\n" +
                        "Telefone: " + request.telefone() + "\n" +
                        "Empresa: " + request.Empresa() + "\n" +
                        "Mensagem: " + request.mensagem())
                .build();

        SendEmailResponse emailSent = emailSender.sendEmail(emailRequest, false);

        contato.setEnviado(emailSent.wasSentSuccessfully());
        intencaoDeContatoRepository.save(contato);

        return ResponseEntity.noContent().build();
    }
}
