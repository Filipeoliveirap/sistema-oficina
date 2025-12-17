package com.oficina.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "unidade_produto")
public class UnidadeProduto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusUnidade status;

    @Column
    private String observacao;

    @ManyToOne
    @JoinColumn(name = "servico_finalizado_id")
    private ServicoFinalizado servicoFinalizado;

    public UnidadeProduto(Produto produto, StatusUnidade status) {
        this.produto = produto;
        this.status = status;
    }

    public enum StatusUnidade {
        DISPONIVEL,
        VENDIDO,
        UTILIZADO
    }
}
