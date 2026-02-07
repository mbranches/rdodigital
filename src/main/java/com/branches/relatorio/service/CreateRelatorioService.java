package com.branches.relatorio.service;

import com.branches.atividade.domain.AtividadeDeRelatorioCampoPersonalizadoEntity;
import com.branches.atividade.domain.AtividadeDeRelatorioEntity;
import com.branches.atividade.repository.AtividadeDeRelatorioRepository;
import com.branches.comentarios.model.ComentarioDeRelatorioEntity;
import com.branches.comentarios.repository.ComentarioDeRelatorioRepository;
import com.branches.condicaoclimatica.domain.CondicaoClimaticaEntity;
import com.branches.equipamento.domain.EquipamentoDeRelatorioEntity;
import com.branches.equipamento.repository.EquipamentoDeRelatorioRepository;
import com.branches.exception.ForbiddenException;
import com.branches.maodeobra.domain.MaoDeObraDeAtividadeDeRelatorioEntity;
import com.branches.maodeobra.domain.MaoDeObraDeRelatorioEntity;
import com.branches.maodeobra.repository.MaoDeObraDeRelatorioRepository;
import com.branches.obra.controller.CheckIfUserHasAccessToObraService;
import com.branches.obra.domain.ConfiguracaoDeAssinaturaDeRelatorioEntity;
import com.branches.obra.domain.ConfiguracaoRelatoriosEntity;
import com.branches.obra.domain.ObraEntity;
import com.branches.obra.service.GetObraByIdExternoAndTenantIdService;
import com.branches.ocorrencia.domain.OcorrenciaDeRelatorioCampoPersonalizadoEntity;
import com.branches.ocorrencia.domain.OcorrenciaDeRelatorioEntity;
import com.branches.ocorrencia.repository.OcorrenciaDeRelatorioRepository;
import com.branches.relatorio.domain.*;
import com.branches.condicaoclimatica.domain.enums.Clima;
import com.branches.condicaoclimatica.domain.enums.CondicaoDoTempo;
import com.branches.relatorio.domain.enums.StatusRelatorio;
import com.branches.relatorio.dto.request.CreateRelatorioRequest;
import com.branches.relatorio.dto.response.CreateRelatorioResponse;
import com.branches.relatorio.repository.*;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional
@RequiredArgsConstructor
@Service
public class CreateRelatorioService {
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final GetObraByIdExternoAndTenantIdService getObraByIdExternoAndTenantIdService;
    private final RelatorioRepository relatorioRepository;
    private final AtividadeDeRelatorioRepository atividadeDeRelatorioRepository;
    private final ComentarioDeRelatorioRepository comentarioDeRelatorioRepository;
    private final MaoDeObraDeRelatorioRepository maoDeObraDeRelatorioRepository;
    private final EquipamentoDeRelatorioRepository equipamentoDeRelatorioRepository;
    private final OcorrenciaDeRelatorioRepository ocorrenciaDeRelatorioRepository;
    private final CheckIfUserHasAccessToObraService checkIfUserHasAccessToObraService;

    public CreateRelatorioResponse execute(CreateRelatorioRequest request, String tenantExternalId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);
        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        checkIfUserCanCreateRelatorio(currentUserTenant);

        ObraEntity obra = getObraByIdExternoAndTenantIdService.execute(request.obraId(), tenantId);
        checkIfUserHasAccessToObraService.execute(currentUserTenant, obra.getId());

        long quantityOfRelatoriosOfObra = relatorioRepository.countByTenantIdAndObraIdAndAtivoIsTrue(tenantId, obra.getId());

        long diferencaEntreDataRelatorioEDataPrevistaFim = ChronoUnit.DAYS.between(request.dataInicio(), obra.getDataPrevistaFim());
        ConfiguracaoRelatoriosEntity configuracaoRelatorios = obra.getConfiguracaoRelatorios();

        RelatorioEntity relatorio = new RelatorioEntity();
        relatorio.setTenantId(tenantId);
        relatorio.setObraId(obra.getId());
        relatorio.setNumero(quantityOfRelatoriosOfObra + 1);
        relatorio.setDataInicio(request.dataInicio());
        relatorio.setDataFim(request.dataFim());
        relatorio.setTipoMaoDeObra(obra.getTipoMaoDeObra());

        List<ConfiguracaoDeAssinaturaDeRelatorioEntity> configuracoesDeAssinaturaDeRelatorio = configuracaoRelatorios.getConfiguracoesDeAssinaturaDeRelatorio();
        List<AssinaturaDeRelatorioEntity> assinaturas = getNewAssinaturas(configuracoesDeAssinaturaDeRelatorio, relatorio);

        relatorio.setAssinaturas(assinaturas);
        relatorio.setPrazoContratualObra(ChronoUnit.DAYS.between(obra.getDataInicio(), obra.getDataPrevistaFim()));
        long diasDecorridos = ChronoUnit.DAYS.between(obra.getDataInicio(), request.dataInicio());
        long prazoDecorridoObra = diasDecorridos < 0 ? 0 : diasDecorridos;
        relatorio.setPrazoDecorridoObra(prazoDecorridoObra);
        relatorio.setPrazoPraVencerObra(diferencaEntreDataRelatorioEDataPrevistaFim < 0 ? 0L : diferencaEntreDataRelatorioEDataPrevistaFim);
        relatorio.setCaracteristicasManha(buildCaracteristicaDefault(tenantId));
        relatorio.setCaracteristicasTarde(buildCaracteristicaDefault(tenantId));
        relatorio.setCaracteristicasNoite(buildCaracteristicaDefault(tenantId));
        relatorio.setStatus(StatusRelatorio.ANDAMENTO);

        RelatorioEntity savedRelatorio = relatorioRepository.save(relatorio);

        if(request.copiarInformacoesDoUltimoRelatorio() && quantityOfRelatoriosOfObra > 0) {
            copyInfoFromLastRelatorio(tenantId, obra.getId(), savedRelatorio, request, configuracaoRelatorios);
        }

        return new CreateRelatorioResponse(savedRelatorio.getIdExterno());
    }

    private List<AssinaturaDeRelatorioEntity> getNewAssinaturas(List<ConfiguracaoDeAssinaturaDeRelatorioEntity> configuracoesDeAssinaturaDeRelatorio, RelatorioEntity relatorio) {
        return configuracoesDeAssinaturaDeRelatorio.stream()
                .map(config -> {
                    AssinaturaDeRelatorioEntity assinatura = AssinaturaDeRelatorioEntity
                            .builder()
                            .configuracao(config)
                            .relatorio(relatorio)
                            .tenantId(relatorio.getTenantId())
                            .build();

                    return assinatura;
                }).toList();
    }

    private CondicaoClimaticaEntity buildCaracteristicaDefault(Long tenantId) {
        return CondicaoClimaticaEntity.builder()
                .condicaoDoTempo(CondicaoDoTempo.PRATICAVEL)
                .clima(Clima.CLARO)
                .tenantId(tenantId)
                .ativo(false)
                .build();
    }

    private void copyInfoFromLastRelatorio(Long tenantId, Long obraId, RelatorioEntity relatorio, CreateRelatorioRequest request, ConfiguracaoRelatoriosEntity configuracaoRelatorios) {
        RelatorioEntity lastRelatorio = relatorioRepository.findFirstByTenantIdAndObraIdAndAtivoIsTrueAndIdIsNotOrderByEnversCreatedDateDesc(tenantId, obraId, relatorio.getId())
                .orElse(null);

        if (lastRelatorio == null) return;

        if (request.copiarHorarioDosTrabalhos()) {
            copyHorarioDeTrabalhoFromLastRelatorio(lastRelatorio, relatorio);
        }

        Map<AtividadeDeRelatorioEntity, AtividadeDeRelatorioEntity> oldAndNewAtividadeMap = new HashMap<>();
        if (request.copiarAtividades() && configuracaoRelatorios.getShowAtividades()) {
            oldAndNewAtividadeMap.putAll(copyAtividadesFromLastRelatorio(lastRelatorio, relatorio));
        }

        if (request.copiarComentarios() && configuracaoRelatorios.getShowComentarios()) {
            copyComentariosFromLastRelatorio(lastRelatorio, relatorio);
        }

        if (request.copiarMaoDeObra() && configuracaoRelatorios.getShowMaoDeObra()) {
            copyMaoDeObraFromLastRelatorio(lastRelatorio, relatorio);
        }

        if (request.copiarEquipamentos() && configuracaoRelatorios.getShowEquipamentos()) {
            copyEquipamentosFromLastRelatorio(lastRelatorio, relatorio);
        }

        if (request.copiarOcorrencias() && configuracaoRelatorios.getShowOcorrencias()) {
            copyOcorrenciasFromLastRelatorio(lastRelatorio, relatorio, oldAndNewAtividadeMap);
        }
    }

    private void copyHorarioDeTrabalhoFromLastRelatorio(RelatorioEntity lastRelatorio, RelatorioEntity relatorio) {
        relatorio.setHoraInicioTrabalhos(lastRelatorio.getHoraInicioTrabalhos());
        relatorio.setHoraFimTrabalhos(lastRelatorio.getHoraFimTrabalhos());
        relatorio.setMinutosIntervalo(lastRelatorio.getMinutosIntervalo());
        relatorio.setMinutosTrabalhados(lastRelatorio.getMinutosTrabalhados());

        relatorioRepository.save(relatorio);
    }

    private void copyOcorrenciasFromLastRelatorio(RelatorioEntity lastRelatorio, RelatorioEntity relatorio, Map<AtividadeDeRelatorioEntity, AtividadeDeRelatorioEntity> oldAndNewAtividadeMap) {
        var ocorrencias = ocorrenciaDeRelatorioRepository.findAllByRelatorioId(lastRelatorio.getId());

        var newOcorrencias = ocorrencias.stream()
                .map(ocorrencia -> {
                    var newOcorrencia = new OcorrenciaDeRelatorioEntity();

                    BeanUtils.copyProperties(ocorrencia, newOcorrencia, "relatorio", "id", "camposPersonalizados");

                    if (ocorrencia.getAtividadeVinculada() != null) {
                        AtividadeDeRelatorioEntity newAtividadeVinculada = oldAndNewAtividadeMap.get(ocorrencia.getAtividadeVinculada());
                        newOcorrencia.setAtividadeVinculada(newAtividadeVinculada);
                    }

                    newOcorrencia.setRelatorio(relatorio);
                    List<CampoPersonalizadoEntity> campoPersonalizadoEntityList = copyCamposPersonalizados(ocorrencia.getCamposPersonalizados().stream().map(OcorrenciaDeRelatorioCampoPersonalizadoEntity::getCampoPersonalizado).toList());
                    newOcorrencia.setCamposPersonalizados(
                            campoPersonalizadoEntityList.stream()
                                    .map(campoPersonalizadoEntity ->
                                            OcorrenciaDeRelatorioCampoPersonalizadoEntity.from(newOcorrencia, campoPersonalizadoEntity, relatorio.getTenantId())
                                    ).toList()
                    );
                    newOcorrencia.setTiposDeOcorrencia(List.copyOf(ocorrencia.getTiposDeOcorrencia()));

                    return newOcorrencia;
                }).toList();

        ocorrenciaDeRelatorioRepository.saveAll(newOcorrencias);
    }

    private void copyEquipamentosFromLastRelatorio(RelatorioEntity lastRelatorio, RelatorioEntity relatorio) {
        var equipamentos = equipamentoDeRelatorioRepository.findAllByRelatorioId(lastRelatorio.getId());

        var newEquipamentos = equipamentos.stream()
                .map(equipamento -> {
                    var newEquipamento = new EquipamentoDeRelatorioEntity();

                    BeanUtils.copyProperties(equipamento, newEquipamento, "relatorio", "id");

                    newEquipamento.setRelatorio(relatorio);

                    return newEquipamento;
                }).toList();

        equipamentoDeRelatorioRepository.saveAll(newEquipamentos);
    }

    private void copyMaoDeObraFromLastRelatorio(RelatorioEntity lastRelatorio, RelatorioEntity relatorio) {
        var maoDeObra = maoDeObraDeRelatorioRepository.findAllByRelatorioId(lastRelatorio.getId());

        var newMaoDeObra = maoDeObra.stream()
                .map(mao -> {
                    var newMao = new MaoDeObraDeRelatorioEntity();

                    BeanUtils.copyProperties(mao, newMao, "relatorio", "id");

                    newMao.setRelatorio(relatorio);

                    return newMao;
                }).toList();

        maoDeObraDeRelatorioRepository.saveAll(newMaoDeObra);
    }


    private void copyComentariosFromLastRelatorio(RelatorioEntity lastRelatorio, RelatorioEntity relatorio) {
        List<ComentarioDeRelatorioEntity> comentariosOfThLastRelatorio = comentarioDeRelatorioRepository.findAllByRelatorioId(lastRelatorio.getId());

        List<ComentarioDeRelatorioEntity> newComentarios = comentariosOfThLastRelatorio.stream()
                .map(comentario -> {
                    var newComentario = new ComentarioDeRelatorioEntity();

                    BeanUtils.copyProperties(comentario, newComentario, "id", "relatorio", "camposPersonalizados");

                    newComentario.setRelatorio(relatorio);

                    return newComentario;
                }).toList();

        comentarioDeRelatorioRepository.saveAll(newComentarios);
    }

    private Map<AtividadeDeRelatorioEntity, AtividadeDeRelatorioEntity> copyAtividadesFromLastRelatorio(RelatorioEntity lastRelatorio, RelatorioEntity relatorio) {
        var atividadesOfTheLastRelatorio = atividadeDeRelatorioRepository.findAllByRelatorioId(lastRelatorio.getId());

        Map<AtividadeDeRelatorioEntity, AtividadeDeRelatorioEntity> oldToNewAtividadeMap = new HashMap<>();

        var newAtividades = atividadesOfTheLastRelatorio.stream()
                .map(atividade -> {
                    var newAtividade = new AtividadeDeRelatorioEntity();

                    BeanUtils.copyProperties(atividade, newAtividade, "id", "relatorio", "camposPersonalizados", "maoDeObra", "ocorrencias");

                    newAtividade.setRelatorio(relatorio);
                    newAtividade.setMaoDeObra(
                            atividade.getMaoDeObra().stream()
                                    .map(maoDeObra -> {
                                                var newMaoDeObra = new MaoDeObraDeAtividadeDeRelatorioEntity();

                                                BeanUtils.copyProperties(maoDeObra, newMaoDeObra, "id", "atividadeDeRelatorio");
                                                newMaoDeObra.setAtividadeDeRelatorio(newAtividade);

                                                return newMaoDeObra;
                                            }
                                    ).toList()
                    );
                    List<CampoPersonalizadoEntity> campoPersonalizadoEntityList = copyCamposPersonalizados(atividade.getCamposPersonalizados().stream().map(AtividadeDeRelatorioCampoPersonalizadoEntity::getCampoPersonalizado).toList());
                    newAtividade.setCamposPersonalizados(
                            campoPersonalizadoEntityList.stream()
                                    .map(campoPersonalizadoEntity ->
                                            AtividadeDeRelatorioCampoPersonalizadoEntity.from(newAtividade, campoPersonalizadoEntity, relatorio.getTenantId())
                                    ).toList()
                    );

                    oldToNewAtividadeMap.put(atividade, newAtividade);

                    return newAtividade;
                }).toList();

        atividadeDeRelatorioRepository.saveAll(newAtividades);

        return oldToNewAtividadeMap;
    }

    private List<CampoPersonalizadoEntity> copyCamposPersonalizados(List<CampoPersonalizadoEntity> camposPersonalizados) {
        return camposPersonalizados.stream()
                .map(cm -> {
                    var newCampo = new CampoPersonalizadoEntity();

                    BeanUtils.copyProperties(cm, newCampo, "id");

                    return newCampo;
                })
                .toList();
    }

    private void checkIfUserCanCreateRelatorio(UserTenantEntity currentUserTenant) {
        if (currentUserTenant.getAuthorities().getRelatorios().getCanCreateAndEdit()) return;

        throw new ForbiddenException();
    }
}
