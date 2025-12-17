package com.oficina.backend.controller;

import com.oficina.backend.DTO.DashBoardDTO;
import com.oficina.backend.DTO.ServicoResumoDTO;
import com.oficina.backend.model.ServicoFinalizado;
import com.oficina.backend.repository.ClienteRepository;
import com.oficina.backend.repository.ProdutoRepository;
import com.oficina.backend.repository.ServicoFinalizadoRepository;
import com.oficina.backend.repository.ServicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
public class DashBoardController {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ServicoRepository servicoRepository;

    @Autowired
    private ServicoFinalizadoRepository servicoFinalizadoRepository;

    @GetMapping
    public DashBoardDTO getDashboardData() {
        DashBoardDTO dto = new DashBoardDTO();

        // Total clientes
        dto.setTotalClientes(clienteRepository.count());

        // Serviços em andamento (contar todos do Servico que ainda não estão finalizados)
        dto.setServicosAndamento(servicoRepository.count());

        // Total de estoque (contar produtos ou somar quantidades, conforme necessidade)
        dto.setTotalEstoque(produtoRepository.count());

        // Serviços finalizados
        dto.setServicosFinalizados(servicoFinalizadoRepository.count());

        // Últimos serviços finalizados (5 mais recentes)
        List<ServicoFinalizado> ultimos = servicoFinalizadoRepository.findTop5ByOrderByDataFinalizacaoDesc();

        // Debug opcional
        ultimos.forEach(s -> {
            System.out.println("Cliente: " + s.getCliente());
            if (s.getCliente() != null) {
                System.out.println("Telefone do cliente: " + s.getCliente().getTelefone());
            }
        });

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        List<ServicoResumoDTO> ultimosDto = ultimos.stream().map(s -> {
            ServicoResumoDTO resumo = new ServicoResumoDTO();
            resumo.setNomeCliente(s.getNomeCliente());
            resumo.setDescricao(s.getDescricao());
            resumo.setData(s.getDataFinalizacao() != null ? s.getDataFinalizacao().toLocalDate().format(formatter) : "");
            resumo.setTelefone(s.getCliente() != null ? s.getCliente().getTelefone() : "");
            resumo.setValor(s.getPreco());
            resumo.setDataInicio(s.getDataInicio() != null ? s.getDataInicio().toLocalDate().format(formatter) : "");
            resumo.setDetalhesFinalizacao(s.getDetalhesFinalizacao());
            resumo.setCpfCliente(s.getCpfCliente());
            resumo.setVeiculo(s.getVeiculo() != null ? s.getVeiculo().getPlaca() : "");

            // Se quiser listar os produtos usados no resumo
            if (s.getUnidadesUsadas() != null) {
                resumo.setProdutosUsados(
                        s.getUnidadesUsadas().stream()
                                .map(u -> u.getProduto().getNome()) // ou outro campo do produto
                                .collect(Collectors.toList())
                );
            }

            resumo.setDataGarantia(s.getDataGarantia());
            resumo.setClausulaGarantia(s.getClausulaGarantia());

            return resumo;
        }).collect(Collectors.toList());


        dto.setUltimosServicos(ultimosDto);

        // Serviços por mês - últimos 4 meses
        LocalDateTime quatroMesesAtras = LocalDateTime.now().withDayOfMonth(1).minusMonths(3);
        List<Object[]> porMes = servicoFinalizadoRepository.contarServicosPorMesUltimos4Meses(quatroMesesAtras);

        Map<String, Long> servicosPorMes = new TreeMap<>();
        for (Object[] obj : porMes) {
            String mes = (String) obj[0];
            Long count = (Long) obj[1];
            if (mes != null) {
                servicosPorMes.put(mes, count);
            }
        }
        dto.setServicosPorMes(servicosPorMes);

        return dto; // <-- ESSENCIAL
    }
}
