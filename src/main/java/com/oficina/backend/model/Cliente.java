package com.oficina.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;
import jakarta.validation.constraints.*;

@Entity
@Data
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @NotBlank(message = "o nome é obrigatório")
    @Size(min = 2, max = 100, message = "O nome deve ter entre 2 a 100 caracteres")
    private String nome;

    @Column
    @NotBlank(message = "O CPF é obrigatório")
    @Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 dígitos numéricos")
    private String cpf;

    @Column
    @NotBlank(message = "O telefone é obrigatório")
    @Pattern(regexp = "\\d{10,11}", message = "O telefone deve conter 10 ou 11 dígitos numéricos")
    private String telefone;

    @Column
    private String email;

    @Column
    private String endereco;

    @OneToMany(mappedBy = "cliente")
    @JsonBackReference
    private List<Servico> servicos;

}