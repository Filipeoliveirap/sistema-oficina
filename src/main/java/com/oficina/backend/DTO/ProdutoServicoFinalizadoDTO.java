package com.oficina.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoServicoFinalizadoDTO {
    private String nome;
    private Long quantidade;
}
