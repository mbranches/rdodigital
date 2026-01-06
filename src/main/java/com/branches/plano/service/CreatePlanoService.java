package com.branches.plano.service;

import com.branches.external.stripe.CreateStripePlano;
import com.branches.external.stripe.CreateStripePlanoResponse;
import com.branches.plano.domain.PlanoEntity;
import com.branches.plano.dto.request.CreatePlanoRequest;
import com.branches.plano.dto.response.PlanoResponse;
import com.branches.plano.repository.PlanoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CreatePlanoService {
    private final CreateStripePlano createStripePlano;
    private final PlanoRepository planoRepository;

    public PlanoResponse execute(CreatePlanoRequest request) {
        CreateStripePlanoResponse stripeResponse = createStripePlano.execute(request);

        PlanoEntity toSave = PlanoEntity.builder()
                .nome(request.nome())
                .descricao(request.descricao())
                .stripeProductId(stripeResponse.productId())
                .stripePriceId(stripeResponse.priceId())
                .valor(request.valor())
                .limiteUsuarios(request.limiteUsuarios())
                .limiteObras(request.limiteObras())
                .recorrencia(request.recorrencia())
                .build();

        PlanoEntity saved = planoRepository.save(toSave);

        return PlanoResponse.from(saved);
    }
}
