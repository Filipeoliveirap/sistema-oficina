package com.oficina.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinalizarServicoDTO {
    private Long servicoId;
    private String detalhesFinalizacao;
    private LocalDate dataGarantia;
    private String clausulaGarantia;
}

