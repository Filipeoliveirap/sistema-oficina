package com.oficina.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Servico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "A descrição do serviço é obrigatória")
    @Column(columnDefinition = "TEXT")
    private String descricao;

    @NotNull(message = "O preço é obrigatório")
    @DecimalMin(value = "0.0", inclusive = false, message = "O preço deve ser maior que zero")
    @Column(columnDefinition = "DECIMAL")
    private BigDecimal preco;

    @NotNull(message = "A data do serviço é obrigatória")
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime data;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    @NotNull(message = "O cliente é obrigatório")
    private Cliente cliente;

    @ManyToMany
    @JoinTable(name = "servico_produto",
            joinColumns = @JoinColumn(name = "servico_id"),
            inverseJoinColumns = @JoinColumn(name = "produto_id"))
    private List<Produto> produtos = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "servico_unidade_produto",
            joinColumns = @JoinColumn(name = "servico_id"),
            inverseJoinColumns = @JoinColumn(name = "unidade_id")
    )
    private List<UnidadeProduto> unidadesUsadas = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "veiculo_id")
    private Veiculo veiculo;


}