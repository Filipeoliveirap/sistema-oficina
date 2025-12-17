package com.oficina.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.oficina.backend.model.Servico;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServicoDTO {
    private Long id;
    private String descricao;
    private BigDecimal preco;
    private LocalDate data;
    private ClienteResumidoDTO cliente;

    private Long veiculoId;
    private String modeloVeiculo;
    private String placaVeiculo;

    public ServicoDTO(Servico servico) {
        this.id = servico.getId();
        this.descricao = servico.getDescricao();
        this.preco = servico.getPreco();
        this.data = servico.getData() != null ? servico.getData().toLocalDate() : null;
        this.cliente = new ClienteResumidoDTO(servico.getCliente());

        if (servico.getVeiculo() != null) {
            this.veiculoId = servico.getVeiculo().getId();
            this.modeloVeiculo = servico.getVeiculo().getModelo();
            this.placaVeiculo = servico.getVeiculo().getPlaca();
        }
    }
}
