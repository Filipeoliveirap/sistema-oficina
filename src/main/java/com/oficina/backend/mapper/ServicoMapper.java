package com.oficina.backend.mapper;

import com.oficina.backend.DTO.*;
import com.oficina.backend.model.Servico;
import com.oficina.backend.model.UnidadeProduto;

import java.util.List;
import java.util.stream.Collectors;

public class ServicoMapper {

    public static ServicoResponseDTO toResponseDTO(Servico servico) {
        ServicoResponseDTO dto = new ServicoResponseDTO();
        dto.setId(servico.getId());
        dto.setDescricao(servico.getDescricao());
        dto.setPreco(servico.getPreco());
        dto.setData(servico.getData());

        // Cliente resumido
        if (servico.getCliente() != null) {
            dto.setCliente(new ClienteResumidoDTO(
                    servico.getCliente().getId(),
                    servico.getCliente().getNome(),
                    servico.getCliente().getCpf()
            ));
        }

        // Veículo resumido
        if (servico.getVeiculo() != null) {
            dto.setVeiculo(new VeiculoResumidoDTO(
                    servico.getVeiculo().getId(),
                    servico.getVeiculo().getModelo(),
                    servico.getVeiculo().getPlaca()
            ));
        }

        // Produtos usados
        List<ProdutoUsadoResponseDTO> produtosUsados = servico.getUnidadesUsadas() != null
                ? servico.getUnidadesUsadas().stream()
                .collect(Collectors.groupingBy(UnidadeProduto::getProduto))
                .entrySet().stream()
                .map(entry -> new ProdutoUsadoResponseDTO(
                        entry.getKey().getId(),
                        entry.getKey().getNome(),
                        entry.getValue().size()
                ))
                .collect(Collectors.toList())
                : List.of();


        dto.setProdutosUsados(produtosUsados);

        return dto;
    }
}
