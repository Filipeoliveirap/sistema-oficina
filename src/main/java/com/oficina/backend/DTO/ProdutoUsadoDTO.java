package com.oficina.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class ProdutoUsadoDTO {
    private Long idUnidade;
    private Long produtoId;
    private String nome;
    private String status;
}
