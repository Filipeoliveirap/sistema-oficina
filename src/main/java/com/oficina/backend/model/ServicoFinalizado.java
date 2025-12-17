package com.oficina.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "servicos_finalizados")
public class ServicoFinalizado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String descricao;

    @Column
    private String detalhesFinalizacao;

    @Column
    private BigDecimal preco;

    @Column
    private LocalDateTime dataInicio;

    @Column
    private LocalDateTime dataFinalizacao;

    @Column
    private String nomeCliente;

    @Column
    private String cpfCliente;

    @Column
    private LocalDate dataGarantia;

    @Column
    private String clausulaGarantia;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "veiculo_id")
    private Veiculo veiculo;

    @ManyToOne
    @JoinColumn(name = "servico_original_id")
    private Servico servicoOriginal;

    @OneToMany(mappedBy = "servicoFinalizado", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UnidadeProduto> unidadesUsadas;
}
