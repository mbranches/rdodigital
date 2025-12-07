package com.branches.relatorio.service;

import com.branches.arquivo.domain.ArquivoEntity;
import com.branches.atividade.domain.AtividadeDeRelatorioEntity;
import com.branches.comentarios.model.ComentarioDeRelatorioEntity;
import com.branches.equipamento.domain.EquipamentoDeRelatorioEntity;
import com.branches.maodeobra.domain.MaoDeObraDeRelatorioEntity;
import com.branches.material.domain.MaterialDeRelatorioEntity;
import com.branches.ocorrencia.domain.OcorrenciaDeRelatorioEntity;
import com.branches.relatorio.domain.ArquivoDeRelatorioDeUsuarioEntity;
import com.branches.relatorio.domain.AssinaturaDeRelatorioEntity;
import com.branches.relatorio.repository.projections.RelatorioDetailsProjection;
import com.branches.usertenant.domain.UserTenantEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Service
public class ProcessRelatorioFileToUsersService {
    private final GenerateRelatorioToUserService generateRelatorioToUserService;

    public List<ArquivoDeRelatorioDeUsuarioEntity> execute(
            RelatorioDetailsProjection details,
            List<UserTenantEntity> userTenants,
            Map<Long, ArquivoDeRelatorioDeUsuarioEntity> mapUserIdAndArquivoDeRelatorio,
            List<OcorrenciaDeRelatorioEntity> ocorrencias,
            List<AtividadeDeRelatorioEntity> atividades,
            List<EquipamentoDeRelatorioEntity> equipamentos,
            List<MaoDeObraDeRelatorioEntity> maoDeObra,
            List<ComentarioDeRelatorioEntity> comentarios,
            List<MaterialDeRelatorioEntity> materiais,
            List<AssinaturaDeRelatorioEntity> assinaturas,
            List<ArquivoEntity> arquivos
    ) {
        Long relatorioId = details.getId();

        boolean relatorioAllowsFotos = details.getShowFotos();
        boolean relatorioAllowsVideos = details.getShowVideos();

        List<ArquivoEntity> fotos = relatorioAllowsFotos ? arquivos.stream().filter(ArquivoEntity::getIsFoto).toList() : Collections.emptyList();
        List<ArquivoEntity> videos = relatorioAllowsVideos ? arquivos.stream().filter(ArquivoEntity::getIsVideo).toList() : Collections.emptyList();

        List<ArquivoDeRelatorioDeUsuarioEntity> arquivoDeRelatorioDeUsuarioToSaveList = new ArrayList<>();

        List<CompletableFuture<Void>> geracoesDosRelatoriosAsync = userTenants.stream()
                .map(userTenant -> CompletableFuture.runAsync(() -> {
                            var userPermissionsItensDeRelatorio = userTenant.getAuthorities().getItensDeRelatorio();

                            boolean userCanViewOcorrencias = userPermissionsItensDeRelatorio.getOcorrencias();
                            boolean userCanViewAtividades = userPermissionsItensDeRelatorio.getAtividades();
                            boolean userCanViewEquipamentos = userPermissionsItensDeRelatorio.getEquipamentos();
                            boolean userCanViewMaoDeObra = userPermissionsItensDeRelatorio.getMaoDeObra();
                            boolean userCanViewComentarios = userPermissionsItensDeRelatorio.getComentarios();
                            boolean userCanViewMateriais = userPermissionsItensDeRelatorio.getMateriais();
                            boolean userCanViewFotos = userPermissionsItensDeRelatorio.getFotos();
                            boolean userCanViewVideos = userPermissionsItensDeRelatorio.getVideos();

                            List<OcorrenciaDeRelatorioEntity> ocorrenciasDoRelatorio = userCanViewOcorrencias ? ocorrencias : Collections.emptyList();
                            List<AtividadeDeRelatorioEntity> atividadesDoRelatorio = userCanViewAtividades ? atividades : Collections.emptyList();
                            List<EquipamentoDeRelatorioEntity> equipamentosDoRelatorio = userCanViewEquipamentos ? equipamentos : Collections.emptyList();
                            List<MaoDeObraDeRelatorioEntity> maoDeObraDoRelatorio = userCanViewMaoDeObra ? maoDeObra : Collections.emptyList();
                            List<ComentarioDeRelatorioEntity> comentariosDoRelatorio = userCanViewComentarios ? comentarios : Collections.emptyList();
                            List<MaterialDeRelatorioEntity> materiaisDoRelatorio = userCanViewMateriais ? materiais : Collections.emptyList();
                            List<ArquivoEntity> fotosDoRelatorio = userCanViewFotos ? fotos : Collections.emptyList();
                            List<ArquivoEntity> videosDoRelatorio = userCanViewVideos ? videos : Collections.emptyList();

                            String url = generateRelatorioToUserService.execute(
                                    details,
                                    userTenant,
                                    ocorrenciasDoRelatorio,
                                    atividadesDoRelatorio,
                                    equipamentosDoRelatorio,
                                    maoDeObraDoRelatorio,
                                    comentariosDoRelatorio,
                                    materiaisDoRelatorio,
                                    fotosDoRelatorio,
                                    videosDoRelatorio,
                                    assinaturas
                            );

                            if (mapUserIdAndArquivoDeRelatorio.containsKey(userTenant.getUser().getId())) return;

                            ArquivoDeRelatorioDeUsuarioEntity toSave = ArquivoDeRelatorioDeUsuarioEntity.builder()
                                    .arquivoUrl(url)
                                    .relatorioId(relatorioId)
                                    .userId(userTenant.getUser().getId())
                                    .build();

                            synchronized (arquivoDeRelatorioDeUsuarioToSaveList) {
                                arquivoDeRelatorioDeUsuarioToSaveList.add(toSave);
                            }
                        }
                ))
                .toList();

        CompletableFuture.allOf(geracoesDosRelatoriosAsync.toArray(new CompletableFuture[0])).join();

        return arquivoDeRelatorioDeUsuarioToSaveList;
    }
}
