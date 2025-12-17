package com.oficina.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServicoResponseDTO {
    private Long id;
    private String descricao;
    private BigDecimal preco;
    private LocalDateTime data;

    private ClienteResumidoDTO cliente;
    private VeiculoResumidoDTO veiculo;
    private List<ProdutoUsadoResponseDTO> produtosUsados;
}

