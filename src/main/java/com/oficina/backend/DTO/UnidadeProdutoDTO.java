package com.oficina.backend.DTO;

import com.oficina.backend.model.UnidadeProduto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnidadeProdutoDTO {
    private Long id;
    private String status;

    public UnidadeProdutoDTO(UnidadeProduto unidade) {
        this.id = unidade.getId();
        this.status = unidade.getStatus().name();
    }
}
