package com.oficina.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
public class Veiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    @NotNull(message = "O cliente é obrigatório")
    private Cliente cliente;

    @Column(nullable = false)
    private String cpfCliente;

    @Column
    @NotBlank(message = "O modelo é obrigatório")
    private String modelo;

    @Column(length = 7)
    @NotBlank(message = "A placa é obrigatória")
    private String placa;

    @Column
    @NotNull(message = "O ano é obrigatório")
    private Integer ano;

    @Column
    @NotBlank(message = "A cor é obrigatória")
    private String cor;

}
