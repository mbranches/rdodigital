package com.branches.comentarios.service;

import com.branches.comentarios.dto.response.ComentarioDeRelatorioResponse;
import com.branches.comentarios.model.ComentarioDeRelatorioEntity;
import com.branches.comentarios.repository.ComentarioDeRelatorioRepository;
import com.branches.obra.service.CheckIfUserHasAccessToObraService;
import com.branches.obra.domain.ObraEntity;
import com.branches.obra.service.GetObraByIdExternoAndTenantIdService;
import com.branches.relatorio.domain.RelatorioEntity;
import com.branches.relatorio.dto.response.ItemPorRelatorioResponse;
import com.branches.shared.pagination.PageResponse;
import com.branches.shared.pagination.PageableRequest;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ListComentariosDeObraPorRelatorioService {
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final GetObraByIdExternoAndTenantIdService getObraByIdExternoAndTenantIdService;
    private final CheckIfUserHasAccessToObraService checkIfUserHasAccessToObraService;
    private final ComentarioDeRelatorioRepository comentarioDeRelatorioRepository;
    private final CheckIfUserCanViewComentariosService checkIfUserCanViewComentariosService;
    private final CheckIfConfiguracaoDeRelatorioDaObraPermiteComentarioService checkIfConfiguracaoDeRelatorioDaObraPermiteComentarioService;

    public PageResponse<ItemPorRelatorioResponse<ComentarioDeRelatorioResponse>> execute(String tenantExternalId, String obraExternalId, List<UserTenantEntity> userTenants, PageableRequest pageableRequest) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        ObraEntity obra = getObraByIdExternoAndTenantIdService.execute(obraExternalId, tenantId);

        checkIfUserHasAccessToObraService.execute(currentUserTenant, obra.getId());
        checkIfUserCanViewComentariosService.execute(currentUserTenant);
        checkIfConfiguracaoDeRelatorioDaObraPermiteComentarioService.execute(obra);

        PageRequest pageRequest = pageableRequest.toPageRequest("dataCriacao");
        Boolean canViewOnlyAprovados = currentUserTenant.getAuthorities().getRelatorios().getCanViewOnlyAprovados();

        Page<ComentarioDeRelatorioEntity> comentarios = comentarioDeRelatorioRepository.findAllByObraIdAndTenantId(obra.getId(), tenantId, canViewOnlyAprovados, pageRequest);

        Map<RelatorioEntity, List<ComentarioDeRelatorioEntity>> MapRelatorioAndComentarios = comentarios.stream()
                .collect(Collectors.groupingBy(ComentarioDeRelatorioEntity::getRelatorio));

        List<ItemPorRelatorioResponse<ComentarioDeRelatorioResponse>> itensPorRelatorio = MapRelatorioAndComentarios.entrySet().stream()
                .map(entry -> {
                    RelatorioEntity relatorio = entry.getKey();
                    List<ComentarioDeRelatorioEntity> comentariosDoRelatorio = entry.getValue();
                    List<ComentarioDeRelatorioResponse> comentariosResponse = comentariosDoRelatorio.stream()
                            .map(ComentarioDeRelatorioResponse::from)
                            .toList();
                    return ItemPorRelatorioResponse.from(relatorio, comentariosResponse);
                })
                .toList();

        return new PageResponse<>(
                comentarios.getSize(),
                comentarios.getNumber(),
                comentarios.getTotalElements(),
                itensPorRelatorio,
                comentarios.isFirst(),
                comentarios.isLast()
        );
    }
}
