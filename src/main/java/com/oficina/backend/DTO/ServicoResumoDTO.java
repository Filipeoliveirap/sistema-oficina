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
public class ServicoResumoDTO {
    private String nomeCliente;
    private String descricao;
    private String data;
    private String dataInicio;
    private String telefone;
    private BigDecimal valor;
    private String detalhesFinalizacao;
    private String cpfCliente;
    private String veiculo;
    private List<String> produtosUsados;
    private LocalDate dataGarantia;
    private String clausulaGarantia;
}
