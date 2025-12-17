package com.oficina.backend.service;

import com.oficina.backend.DTO.FinalizarServicoDTO;
import com.oficina.backend.model.UnidadeProduto;
import com.oficina.backend.model.Servico;
import com.oficina.backend.model.ServicoFinalizado;
import com.oficina.backend.repository.ServicoFinalizadoRepository;
import com.oficina.backend.repository.ServicoRepository;
import com.oficina.backend.repository.UnidadeProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ServicoFinalizadoService {

    @Autowired
    private ServicoFinalizadoRepository servicoFinalizadoRepository;

    @Autowired
    private ServicoRepository servicoRepository;

    @Autowired
    private UnidadeProdutoRepository unidadeProdutoRepository;

    // Listar serviços finalizados com filtros e paginação
    public Page<ServicoFinalizado> buscarServicosFinalizados(String termo, LocalDate inicio, LocalDate fim, String periodo, Pageable pageable) {
        LocalDateTime dataInicio = null;
        LocalDateTime dataFim = null;

        if (inicio != null && fim != null) {
            dataInicio = inicio.atStartOfDay();
            dataFim = fim.atTime(LocalTime.MAX);
        } else if (periodo != null && !periodo.trim().isEmpty()) {
            LocalDate hoje = LocalDate.now();
            switch (periodo.toLowerCase()) {
                case "semana":
                    dataInicio = hoje.with(DayOfWeek.MONDAY).atStartOfDay();
                    dataFim = hoje.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).atTime(LocalTime.MAX);
                    break;
                case "mes":
                    dataInicio = hoje.withDayOfMonth(1).atStartOfDay();
                    dataFim = hoje.withDayOfMonth(hoje.lengthOfMonth()).atTime(LocalTime.MAX);
                    break;
                case "ano":
                    dataInicio = hoje.withDayOfYear(1).atStartOfDay();
                    dataFim = hoje.withDayOfYear(hoje.lengthOfYear()).atTime(LocalTime.MAX);
                    break;
                case "todos":
                default:
                    dataInicio = null;
                    dataFim = null;
                    break;
            }
        }

        if (termo != null && termo.trim().isEmpty()) {
            termo = null;
        }

        return servicoFinalizadoRepository.buscarComFiltros(termo, dataInicio, dataFim, pageable);
    }

    // Finalizar serviço e criar registro em ServicoFinalizado
    public ServicoFinalizado finalizarServico(Long id, FinalizarServicoDTO dto) {
        Optional<Servico> servicoOpt = servicoRepository.findById(id);
        if (servicoOpt.isEmpty()) {
            return null;
        }

        Servico servico = servicoOpt.get();

        ServicoFinalizado finalizado = new ServicoFinalizado();
        finalizado.setCliente(servico.getCliente());
        finalizado.setVeiculo(servico.getVeiculo());
        finalizado.setDescricao(servico.getDescricao());
        finalizado.setPreco(servico.getPreco());
        finalizado.setDataInicio(servico.getData());
        finalizado.setDataFinalizacao(LocalDateTime.now());
        finalizado.setDetalhesFinalizacao(dto.getDetalhesFinalizacao());
        finalizado.setDataGarantia(dto.getDataGarantia());
        finalizado.setClausulaGarantia(dto.getClausulaGarantia());
        finalizado.setServicoOriginal(servico);

        List<UnidadeProduto> copiaUnidades = new ArrayList<>();
        for (UnidadeProduto unidade : servico.getUnidadesUsadas()) {
            UnidadeProduto novaUnidade = new UnidadeProduto();
            novaUnidade.setProduto(unidade.getProduto());
            novaUnidade.setStatus(unidade.getStatus());
            novaUnidade.setServicoFinalizado(finalizado);
            copiaUnidades.add(novaUnidade);
        }
        finalizado.setUnidadesUsadas(copiaUnidades);

        // Salvar primeiro o finalizado
        ServicoFinalizado salvo = servicoFinalizadoRepository.save(finalizado);

        // Depois remover o serviço original
        servicoRepository.delete(servico);

        return salvo;
    }



    // Buscar serviço finalizado por ID
    public Optional<ServicoFinalizado> buscarPorId(Long id) {
        return servicoFinalizadoRepository.findById(id);
    }

    // Buscar o último ServicoFinalizado por serviço original
    public ServicoFinalizado buscarUltimoFinalizadoPorServicoOriginal(Long servicoId) {
        return servicoFinalizadoRepository.findTopByServicoOriginalIdOrderByDataFinalizacaoDesc(servicoId)
                .orElseThrow(() -> new RuntimeException("Serviço finalizado não encontrado para o serviço original com id: " + servicoId));
    }
}
