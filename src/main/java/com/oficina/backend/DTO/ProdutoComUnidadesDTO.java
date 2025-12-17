package com.oficina.backend.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
public class ProdutoComUnidadesDTO {
    private Long id;
    private String nome;
    private int quantidade;
    private BigDecimal precoUnitario;
    private String categoria;
    private String observacao;
    private List<UnidadeProdutoDTO> unidades;
}
