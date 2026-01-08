package com.branches.assinatura.service;

import com.branches.assinatura.domain.AssinaturaEntity;
import com.branches.assinatura.domain.enums.AssinaturaStatus;
import com.branches.assinatura.repository.AssinaturaRepository;
import com.branches.exception.NotFoundException;
import com.branches.plano.domain.PlanoEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAssinaturaActiveByTenantIdServiceTest {
    @InjectMocks
    private GetAssinaturaActiveByTenantIdService service;
    @Mock
    private AssinaturaRepository assinaturaRepository;

    private Long tenantId;
    private PlanoEntity plano;
    private AssinaturaEntity assinatura;

    @BeforeEach
    void setup() {
        plano = PlanoEntity.builder()
                .id(2L)
                .nome("Plano Premium")
                .descricao("Acesso completo a todos os recursos")
                .valor(BigDecimal.valueOf(99.90))
                .build();

        assinatura = AssinaturaEntity.builder()
                .id(1L)
                .tenantId(1L)
                .plano(plano)
                .status(AssinaturaStatus.ATIVO)
                .dataInicio(LocalDate.of(2025, 1, 1))
                .dataFim(LocalDate.of(2026, 1, 1))
                .build();
        tenantId = 1L;
    }

    @Test
    void deveExecutarGetAssinaturaByTenantIdComSucesso() {
        when(assinaturaRepository.findByStatusInAndTenantId(AssinaturaStatus.ATIVO, tenantId))
            .thenReturn(Optional.of(assinatura));

        AssinaturaEntity response = service.execute(tenantId);

        assertNotNull(response);
        assertEquals(assinatura.getId(), response.getId());
        assertEquals(assinatura.getTenantId(), response.getTenantId());
        assertEquals(assinatura.getPlano().getId(), response.getPlano().getId());
    }

    @Test
    void deveLancarNotFoundExceptionQuandoAssinaturaNaoExistir() {
        when(assinaturaRepository.findByStatusInAndTenantId(AssinaturaStatus.ATIVO, tenantId))
            .thenReturn(Optional.empty());

         NotFoundException exception = assertThrows(NotFoundException.class, () -> service.execute(tenantId));

         String expectedMessage = "Assinatura ativa não encontrada para o tenantId: " + tenantId;

         assertNotNull(exception);
         assertEquals(expectedMessage, exception.getReason());
    }
}