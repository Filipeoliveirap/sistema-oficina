package com.oficina.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServicoRequestDTO {
    private String descricao;
    private BigDecimal preco;
    private LocalDate data;
    private ClienteRequestDTO cliente;
    private VeiculoRequestDTO veiculo;
    private List<ProdutoServicoDTO> produtosUsados;
}
