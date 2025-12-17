package com.oficina.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashBoardDTO {
    private long totalClientes;
    private long servicosAndamento;
    private long totalEstoque;
    private long servicosFinalizados;

    private List<ServicoResumoDTO> ultimosServicos;
    private Map<String, Long> servicosPorMes;
}
