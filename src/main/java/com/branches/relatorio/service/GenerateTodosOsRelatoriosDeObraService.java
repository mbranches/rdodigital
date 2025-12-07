package com.branches.relatorio.service;

import com.branches.arquivo.domain.ArquivoEntity;
import com.branches.arquivo.repository.ArquivoRepository;
import com.branches.atividade.domain.AtividadeDeRelatorioEntity;
import com.branches.atividade.repository.AtividadeDeRelatorioRepository;
import com.branches.comentarios.model.ComentarioDeRelatorioEntity;
import com.branches.comentarios.repository.ComentarioDeRelatorioRepository;
import com.branches.equipamento.domain.EquipamentoDeRelatorioEntity;
import com.branches.equipamento.repository.EquipamentoDeRelatorioRepository;
import com.branches.maodeobra.domain.MaoDeObraDeRelatorioEntity;
import com.branches.maodeobra.repository.MaoDeObraDeRelatorioRepository;
import com.branches.material.domain.MaterialDeRelatorioEntity;
import com.branches.material.repository.MaterialDeRelatorioRepository;
import com.branches.obra.domain.ObraEntity;
import com.branches.ocorrencia.domain.OcorrenciaDeRelatorioEntity;
import com.branches.ocorrencia.repository.OcorrenciaDeRelatorioRepository;
import com.branches.relatorio.domain.ArquivoDeRelatorioDeUsuarioEntity;
import com.branches.relatorio.domain.AssinaturaDeRelatorioEntity;
import com.branches.relatorio.repository.ArquivoDeRelatorioDeUsuarioRepository;
import com.branches.relatorio.repository.AssinaturaDeRelatorioRepository;
import com.branches.relatorio.repository.RelatorioRepository;
import com.branches.relatorio.repository.projections.RelatorioDetailsProjection;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.repository.UserTenantRepository;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Serviço para gerar todos os relatórios pra todos os usuários de uma obra específica.]
 * Isso é feito de forma Async
 * São processados 20 relatórios por vez, em paralelo, para otimizar o uso de recursos.
 * Caso um novo processamento para uma obra que ja esta processando, o processamento anterior é cancelado.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GenerateTodosOsRelatoriosDeObraService {
    private final RelatorioRepository relatorioRepository;
    private final OcorrenciaDeRelatorioRepository ocorrenciaDeRelatorioRepository;
    private final AtividadeDeRelatorioRepository atividadeDeRelatorioRepository;
    private final EquipamentoDeRelatorioRepository equipamentoDeRelatorioRepository;
    private final MaoDeObraDeRelatorioRepository maoDeObraDeRelatorioRepository;
    private final ComentarioDeRelatorioRepository comentarioDeRelatorioRepository;
    private final MaterialDeRelatorioRepository materialDeRelatorioRepository;
    private final ArquivoRepository arquivoRepository;
    private final AssinaturaDeRelatorioRepository assinaturaDeRelatorioRepository;
    private final ProcessRelatorioFileToUsersService processRelatorioFileToUsersService;
    private final UserTenantRepository userTenantRepository;
    private final ArquivoDeRelatorioDeUsuarioRepository arquivoDeRelatorioDeUsuarioRepository;
    private final ExecutorService pdfExecutor = Executors.newFixedThreadPool(20); // Executor para processamento paralelo, são reservados 20 threads

    // Map para rastrear processamentos em andamento por obra
    private final Map<Long, CompletableFuture<Void>> processamentosEmAndamento = new ConcurrentHashMap<>();

    @PreDestroy
    public void shutdown() {
        log.info("Encerrando executor de PDFs");
        pdfExecutor.shutdown();
        try {
            if (!pdfExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
                pdfExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            log.warn("Shutdown do executor foi interrompido. Forçando encerramento imediato.");
            pdfExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    @Async
    public void execute(ObraEntity obra) {
        Long obraId = obra.getId();

        // Cancelar processamento anterior se existir
        CompletableFuture<Void> processamentoAnterior = processamentosEmAndamento.get(obraId);
        if (processamentoAnterior != null && !processamentoAnterior.isDone()) {
            log.warn("Cancelando processamento em andamento para obra ID: {}. Novo processamento será iniciado.", obraId);
            processamentoAnterior.cancel(true);
        }

        // Criar novo CompletableFuture para rastrear este processamento
        CompletableFuture<Void> novoProcessamento = CompletableFuture.runAsync(() -> {
            try {
                processarRelatorios(obra);
            } catch (Exception e) {
                if (e instanceof CancellationException) {
                    log.info("Processamento da obra ID {} foi cancelado", obraId);
                } else {
                    log.error("Erro ao processar relatórios da obra ID {}", obraId, e);
                }
            } finally {
                processamentosEmAndamento.remove(obraId);
            }
        });

        processamentosEmAndamento.put(obraId, novoProcessamento);
    }

    private void processarRelatorios(ObraEntity obra) {
        Long obraId = obra.getId();
        log.info("Iniciando geração de todos os relatórios da obra ID: {}", obraId);
        List<RelatorioDetailsProjection> relatorios = relatorioRepository.findAllDetailsWithoutPdfLinkByObraId(obraId);

        Map<Long, List<OcorrenciaDeRelatorioEntity>> mapRelatorioIdAndOcorrencias = getRelatorioIdAndOcorrenciasMap(relatorios);
        Map<Long, List<AtividadeDeRelatorioEntity>> mapRelatorioIdAndAtividades = getRelatorioIdAndAtividadesMap(relatorios);
        Map<Long, List<EquipamentoDeRelatorioEntity>> mapRelatorioIdAndEquipamentos = getRelatorioIdAndEquipamentosMap(relatorios);
        Map<Long, List<MaoDeObraDeRelatorioEntity>> mapRelatorioIdAndMaoDeObra = getRelatorioIdAndMaoDeObraMap(relatorios);
        Map<Long, List<ComentarioDeRelatorioEntity>> mapRelatorioIdAndComentarios = getRelatorioIdAndComentariosMap(relatorios);
        Map<Long, List<MaterialDeRelatorioEntity>> mapRelatorioIdAndMateriais = getRelatorioIdAndMateriaisMap(relatorios);
        Map<Long, List<AssinaturaDeRelatorioEntity>> mapRelatorioIdAndAssinaturas = getRelatorioIdAndAssinaturasMap(relatorios);
        Map<Long, List<ArquivoEntity>> mapRelatorioIdAndArquivos = getRelatorioIdAndArquivosMap(relatorios);

        List<UserTenantEntity> userTenantsWithAccessToObra = userTenantRepository.findAllByTenantIdAndUserHasAccessToObraId(obra.getTenantId(), obraId);
        Map<Long, Map<Long, ArquivoDeRelatorioDeUsuarioEntity>> mapRelatorioIdAndMapUserAndHisArquivoDeRelatorio = getRelatorioIdAndMapOfUserIdAndArquivoDeRelatorioMap(relatorios, userTenantsWithAccessToObra);

        List<ArquivoDeRelatorioDeUsuarioEntity> allArquivosToSave = new ArrayList<>();

        log.info("Processando {} relatórios em lotes...", relatorios.size());
        // Processar relatórios em lotes de 20
        int batchSize = 20;
        AtomicInteger batchNumber = new AtomicInteger(0);
        for (int i = 0; i < relatorios.size(); i += batchSize) {
            // Verificar se o processamento foi cancelado
            if (Thread.currentThread().isInterrupted()) {
                log.warn("Processamento da obra ID {} foi interrompido no lote {}", obraId, batchNumber.get());
                throw new CancellationException("Processamento cancelado");
            }

            log.info("Processando lote {}", batchNumber.getAndIncrement());
            int endIndex = Math.min(i + batchSize, relatorios.size());
            List<RelatorioDetailsProjection> batch = relatorios.subList(i, endIndex);

            List<CompletableFuture<Void>> batchFutures = batch.stream()
                    .map(relatorio -> CompletableFuture.runAsync(() -> {
                        // Verificar interrupção antes de processar cada relatório
                        if (Thread.currentThread().isInterrupted()) {
                            throw new CancellationException("Processamento cancelado");
                        }

                        try {
                            List<ArquivoDeRelatorioDeUsuarioEntity> arquivosToSave = processRelatorioFileToUsersService.execute(
                                    relatorio,
                                    userTenantsWithAccessToObra,
                                    mapRelatorioIdAndMapUserAndHisArquivoDeRelatorio.get(relatorio.getId()),
                                    mapRelatorioIdAndOcorrencias.get(relatorio.getId()),
                                    mapRelatorioIdAndAtividades.get(relatorio.getId()),
                                    mapRelatorioIdAndEquipamentos.get(relatorio.getId()),
                                    mapRelatorioIdAndMaoDeObra.get(relatorio.getId()),
                                    mapRelatorioIdAndComentarios.get(relatorio.getId()),
                                    mapRelatorioIdAndMateriais.get(relatorio.getId()),
                                    mapRelatorioIdAndAssinaturas.get(relatorio.getId()),
                                    mapRelatorioIdAndArquivos.get(relatorio.getId())
                            );

                            synchronized (allArquivosToSave) {
                                allArquivosToSave.addAll(arquivosToSave);
                            }
                        } catch (CancellationException e) {
                            throw e; // Re-lançar para propagar o cancelamento
                        } catch (Exception e) {
                            log.error("Erro ao processar relatório ID {}: {}", relatorio.getId(), e.getMessage());
                        }
                    }, pdfExecutor)).toList();

            try {
                CompletableFuture.allOf(batchFutures.toArray(new CompletableFuture[0])).join();
            } catch (CancellationException | CompletionException e) {
                if (e.getCause() instanceof CancellationException || e instanceof CancellationException) {
                    log.warn("Lote {} cancelado para obra ID {}", batchNumber.get() - 1, obraId);
                    throw new CancellationException("Processamento cancelado");
                }
                throw e;
            }
        }
        log.info("Relatórios processados. Salvando {} arquivos de relatórios de usuários no banco de dados...", allArquivosToSave.size());
        arquivoDeRelatorioDeUsuarioRepository.saveAll(allArquivosToSave);
        log.info("Geração de todos os relatórios da obra ID {} concluída com sucesso. Total de arquivos salvos: {}", obra.getId(), allArquivosToSave.size());
    }

    private Map<Long, Map<Long, ArquivoDeRelatorioDeUsuarioEntity>> getRelatorioIdAndMapOfUserIdAndArquivoDeRelatorioMap(List<RelatorioDetailsProjection> relatorios, List<UserTenantEntity> userTenantsWithAccessToObra) {
        List<Long> relatorioIds = relatorios.stream().map(RelatorioDetailsProjection::getId).toList();
        List<Long> userIds = userTenantsWithAccessToObra.stream().map(ut -> ut.getUser().getId()).toList();

        List<ArquivoDeRelatorioDeUsuarioEntity> arquivos = arquivoDeRelatorioDeUsuarioRepository.findAllByRelatorioIdInAndUserIdIn(relatorioIds, userIds);

        return relatorioIds.stream()
                .collect(Collectors.toMap(
                        relatorioId -> relatorioId,
                        relatorioId -> arquivos.stream()
                                .filter(a -> a.getRelatorioId().equals(relatorioId))
                                .collect(Collectors.toMap(ArquivoDeRelatorioDeUsuarioEntity::getUserId, a -> a))
                        )
                );
    }

    private Map<Long, List<AssinaturaDeRelatorioEntity>> getRelatorioIdAndAssinaturasMap(List<RelatorioDetailsProjection> relatorios) {
        List<Long> relatoriosIdsToQuery = relatorios.stream()
                .map(RelatorioDetailsProjection::getId)
                .toList();
        List<AssinaturaDeRelatorioEntity> assinaturas = assinaturaDeRelatorioRepository.findAllByRelatorioIdIn(relatoriosIdsToQuery);

        Map<Long, List<AssinaturaDeRelatorioEntity>> map = relatorios.stream()
                .map(RelatorioDetailsProjection::getId)
                .collect(Collectors.toMap(id -> id, id -> new ArrayList<>()));

        assinaturas.forEach(assinatura ->
                map.get(assinatura.getRelatorio().getId()).add(assinatura)
        );

        return map;
    }

    private Map<Long, List<ArquivoEntity>> getRelatorioIdAndArquivosMap(List<RelatorioDetailsProjection> relatorios) {
        List<Long> relatoriosIdsToQuery = relatorios.stream()
                //todo: quando adicionar anexo, adicionar aqui também
                .filter(r -> r.getShowFotos() || r.getShowVideos()) //todo: quando for pegar a lista de fotos ou videos, fazer a checagem individualmente
                .map(RelatorioDetailsProjection::getId)
                .toList();
        List<ArquivoEntity> arquivos = arquivoRepository.findAllByRelatorioIdIn(relatoriosIdsToQuery);

        Map<Long, List<ArquivoEntity>> map = relatorios.stream()
                .map(RelatorioDetailsProjection::getId)
                .collect(Collectors.toMap(id -> id, id -> new ArrayList<>()));

        arquivos.forEach(arquivo ->
                map.get(arquivo.getRelatorio().getId()).add(arquivo)
        );

        return map;
    }

    private Map<Long, List<MaterialDeRelatorioEntity>> getRelatorioIdAndMateriaisMap(List<RelatorioDetailsProjection> relatorios) {
        List<Long> relatoriosIdsToQuery = relatorios.stream()
                .filter(RelatorioDetailsProjection::getShowMateriais)
                .map(RelatorioDetailsProjection::getId)
                .toList();
        List<MaterialDeRelatorioEntity> materiais = materialDeRelatorioRepository.findAllByRelatorioIdIn(relatoriosIdsToQuery);

        Map<Long, List<MaterialDeRelatorioEntity>> map = relatorios.stream()
                .map(RelatorioDetailsProjection::getId)
                .collect(Collectors.toMap(id -> id, id -> new ArrayList<>()));

        materiais.forEach(material ->
                map.get(material.getRelatorio().getId()).add(material)
        );

        return map;
    }

    private Map<Long, List<ComentarioDeRelatorioEntity>> getRelatorioIdAndComentariosMap(List<RelatorioDetailsProjection> relatorios) {
        List<Long> relatoriosIdsToQuery = relatorios.stream()
                .filter(RelatorioDetailsProjection::getShowComentarios)
                .map(RelatorioDetailsProjection::getId)
                .toList();
        List<ComentarioDeRelatorioEntity> comentarios = comentarioDeRelatorioRepository.findAllByRelatorioIdIn(relatoriosIdsToQuery);

        Map<Long, List<ComentarioDeRelatorioEntity>> map = relatorios.stream()
                .map(RelatorioDetailsProjection::getId)
                .collect(Collectors.toMap(id -> id, id -> new ArrayList<>()));

        comentarios.forEach(comentario ->
                map.get(comentario.getRelatorio().getId()).add(comentario)
        );

        return map;
    }

    private Map<Long, List<MaoDeObraDeRelatorioEntity>> getRelatorioIdAndMaoDeObraMap(List<RelatorioDetailsProjection> relatorios) {
        List<Long> relatoriosIdsToQuery = relatorios.stream()
                .filter(RelatorioDetailsProjection::getShowMaoDeObra)
                .map(RelatorioDetailsProjection::getId)
                .toList();
        List<MaoDeObraDeRelatorioEntity> maoDeObra = maoDeObraDeRelatorioRepository.findAllByRelatorioIdIn(relatoriosIdsToQuery);

        Map<Long, List<MaoDeObraDeRelatorioEntity>> map = relatorios.stream()
                .map(RelatorioDetailsProjection::getId)
                .collect(Collectors.toMap(id -> id, id -> new ArrayList<>()));

        maoDeObra.forEach(mao ->
                map.get(mao.getRelatorio().getId()).add(mao)
        );

        return map;
    }

    private Map<Long, List<EquipamentoDeRelatorioEntity>> getRelatorioIdAndEquipamentosMap(List<RelatorioDetailsProjection> relatorios) {
        List<Long> relatoriosIdsToQuery = relatorios.stream()
                .filter(RelatorioDetailsProjection::getShowEquipamentos)
                .map(RelatorioDetailsProjection::getId)
                .toList();
        List<EquipamentoDeRelatorioEntity> equipamentos = equipamentoDeRelatorioRepository.findAllByRelatorioIdIn(relatoriosIdsToQuery);

        Map<Long, List<EquipamentoDeRelatorioEntity>> map = relatorios.stream()
                .map(RelatorioDetailsProjection::getId)
                .collect(Collectors.toMap(id -> id, id -> new ArrayList<>()));

        equipamentos.forEach(equipamento ->
                map.get(equipamento.getRelatorio().getId()).add(equipamento)
        );

        return map;
    }

    private Map<Long, List<OcorrenciaDeRelatorioEntity>> getRelatorioIdAndOcorrenciasMap(List<RelatorioDetailsProjection> relatorios) {
        List<Long> relatoriosIdsToQuery = relatorios.stream()
                .filter(RelatorioDetailsProjection::getShowOcorrencias)
                .map(RelatorioDetailsProjection::getId)
                .toList();
        List<OcorrenciaDeRelatorioEntity> ocorrencias = ocorrenciaDeRelatorioRepository.findAllByRelatorioIdIn(relatoriosIdsToQuery);

        Map<Long, List<OcorrenciaDeRelatorioEntity>> map = relatorios.stream()
                .map(RelatorioDetailsProjection::getId)
                .collect(Collectors.toMap(id -> id, id -> new ArrayList<>()));

        ocorrencias.forEach(ocorrencia ->
                map.get(ocorrencia.getRelatorio().getId()).add(ocorrencia)
        );

        return map;
    }

    private Map<Long, List<AtividadeDeRelatorioEntity>> getRelatorioIdAndAtividadesMap(List<RelatorioDetailsProjection> relatorios) {
        List<Long> relatoriosIdsToQuery = relatorios.stream()
                .filter(RelatorioDetailsProjection::getShowAtividades)
                .map(RelatorioDetailsProjection::getId)
                .toList();
        List<AtividadeDeRelatorioEntity> atividades = atividadeDeRelatorioRepository.findAllByRelatorioIdIn(relatoriosIdsToQuery);

        Map<Long, List<AtividadeDeRelatorioEntity>> map = relatorios.stream()
                .map(RelatorioDetailsProjection::getId)
                .collect(Collectors.toMap(id -> id, id -> new ArrayList<>()));

        atividades.forEach(atividade ->
                map.get(atividade.getRelatorio().getId()).add(atividade)
        );

        return map;
    }
}
