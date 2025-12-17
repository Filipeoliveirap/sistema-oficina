package com.oficina.backend.DTO;

import com.oficina.backend.model.ServicoFinalizado;
import com.oficina.backend.model.UnidadeProduto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors; // <-- IMPORTANTE

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServicoFinalizadoDTO {
    private Long id;
    private String descricao;
    private String detalhesFinalizacao;
    private BigDecimal preco;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFinalizacao;

    private String nomeCliente;
    private String cpfCliente;

    private LocalDate dataGarantia;
    private String clausulaGarantia;

    private ClienteResumidoDTO cliente;
    private VeiculoResumidoDTO veiculo;

    private List<Long> unidadesUsadasIds;

    private List<ProdutoUsadoDTO> produtosUsados;
    private List<ProdutoServicoFinalizadoDTO> quantidadeProdutosUsados;

    public ServicoFinalizadoDTO(ServicoFinalizado s) {
        this.id = s.getId();
        this.descricao = s.getDescricao();
        this.detalhesFinalizacao = s.getDetalhesFinalizacao();
        this.preco = s.getPreco();
        this.dataInicio = s.getDataInicio();
        this.dataFinalizacao = s.getDataFinalizacao();

        this.nomeCliente = s.getNomeCliente();
        this.cpfCliente = s.getCpfCliente();

        this.dataGarantia = s.getDataGarantia();
        this.clausulaGarantia = s.getClausulaGarantia();

        // usa DTOs resumidos corretamente
        if (s.getCliente() != null) {
            this.cliente = new ClienteResumidoDTO(
                    s.getCliente().getId(),
                    s.getCliente().getNome(),
                    s.getCliente().getCpf()
            );
        }
        if (s.getVeiculo() != null) {
            this.veiculo = new VeiculoResumidoDTO(
                    s.getVeiculo().getId(),
                    s.getVeiculo().getModelo(),
                    s.getVeiculo().getPlaca()
            );
        }

        // ids simples
        this.unidadesUsadasIds = s.getUnidadesUsadas() == null ? List.of()
                : s.getUnidadesUsadas().stream()
                .map(UnidadeProduto::getId)
                .toList();

        // lista enriquecida para o front
        this.produtosUsados = s.getUnidadesUsadas() == null ? List.of()
                : s.getUnidadesUsadas().stream().map(u ->
                new ProdutoUsadoDTO(
                        u.getId(),
                        u.getProduto() != null ? u.getProduto().getId() : null,
                        u.getProduto() != null ? u.getProduto().getNome() : null,
                        u.getStatus() != null ? u.getStatus().name() : null
                )
        ).toList();

        // agrupando e contando quantidade por produto
        this.quantidadeProdutosUsados = s.getUnidadesUsadas() == null ? List.of()
                : s.getUnidadesUsadas().stream()
                .collect(Collectors.groupingBy(
                        unidade -> unidade.getProduto().getNome(),
                        Collectors.counting()
                ))
                .entrySet().stream()
                .map(entry -> new ProdutoServicoFinalizadoDTO(entry.getKey(), entry.getValue()))
                .toList();
    }
}
