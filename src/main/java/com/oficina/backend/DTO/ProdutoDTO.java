package com.oficina.backend.DTO;

import com.oficina.backend.model.Produto;
import com.oficina.backend.model.UnidadeProduto;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoDTO {
    private Long id;

    @NotBlank(message = "O nome do produto é obrigatório")
    private String nome;

    @NotNull(message = "A quantidade é obrigatória")
    @Min(value = 0, message = "A quantidade não pode ser negativa")
    private Integer quantidade;

    @NotNull(message = "O preço unitário é obrigatório")
    @DecimalMin(value = "0.0", inclusive = false, message = "O preço deve ser maior que zero")
    private BigDecimal precoUnitario;

    @NotBlank(message = "A categoria é obrigatória")
    private String categoria;

    private String observacao;

    @jakarta.validation.constraints.Pattern(
            regexp = "disponivel|vendido|utilizado",
            message = "O status deve ser 'disponivel', 'vendido' ou 'utilizado'"
    )
    private String status = "disponivel";

    public ProdutoDTO(Produto produto) {
        this.id = produto.getId();
        this.nome = produto.getNome();
        this.quantidade = (int) produto.getUnidades().stream()
                .filter(u -> u.getStatus() == UnidadeProduto.StatusUnidade.DISPONIVEL)
                .count();
        this.precoUnitario = produto.getPrecoUnitario();
        this.categoria = produto.getCategoria();
        this.observacao = produto.getObservacao();
        // converter status para string minúscula, por exemplo "disponivel"
        this.status = produto.getUnidades().stream()
                .filter(u -> u.getStatus() == UnidadeProduto.StatusUnidade.DISPONIVEL)
                .findAny()
                .map(u -> u.getStatus().name().toLowerCase())
                .orElse("indefinido");
    }


}

