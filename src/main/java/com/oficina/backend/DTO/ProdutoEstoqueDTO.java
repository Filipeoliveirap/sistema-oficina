package com.oficina.backend.DTO;

import com.oficina.backend.model.Produto;
import com.oficina.backend.model.UnidadeProduto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoEstoqueDTO {
    private Long id;
    private String nome;
    private String categoria;
    private String observacao;
    private String status;
    private BigDecimal precoUnitario;

    private int quantidade;
    private int quantidadeDisponivel;
    private int quantidadeVendida;
    private int quantidadeUtilizada;

    public ProdutoEstoqueDTO(Produto produto) {
        this.id = produto.getId();
        this.nome = produto.getNome();
        this.categoria = produto.getCategoria();
        this.observacao = produto.getObservacao();
        this.precoUnitario = produto.getPrecoUnitario();

        int disponiveis = (int) produto.getUnidades().stream()
                .filter(u -> u.getStatus() == UnidadeProduto.StatusUnidade.DISPONIVEL)
                .count();

        int vendidos = (int) produto.getUnidades().stream()
                .filter(u -> u.getStatus() == UnidadeProduto.StatusUnidade.VENDIDO)
                .count();

        int utilizados = (int) produto.getUnidades().stream()
                .filter(u -> u.getStatus() == UnidadeProduto.StatusUnidade.UTILIZADO)
                .count();

        this.quantidadeDisponivel = disponiveis;
        this.quantidadeVendida = vendidos;
        this.quantidadeUtilizada = utilizados;

        this.quantidade = disponiveis + vendidos + utilizados;

        if (disponiveis == produto.getUnidades().size()) {
            this.status = "disponivel";
        } else if (vendidos == produto.getUnidades().size()) {
            this.status = "vendido";
        } else if (utilizados == produto.getUnidades().size()) {
            this.status = "utilizado";
        } else {
            this.status = "misto";
        }
    }
}
