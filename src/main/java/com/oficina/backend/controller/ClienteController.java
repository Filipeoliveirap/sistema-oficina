package com.oficina.backend.controller;

import com.oficina.backend.model.Cliente;
import com.oficina.backend.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page; //
import java.util.List;
import com.oficina.backend.exception.CpfDuplicadoException;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    // Criar novo cliente
    @PostMapping
    public ResponseEntity<?> salvar(@RequestBody Cliente cliente) {
        try {
            // Verifica se o cliente já existe, e se o CPF é duplicado
            Cliente clienteAtualizado = clienteService.salvar(cliente);
            return ResponseEntity.status(HttpStatus.CREATED).body(clienteAtualizado);
        } catch (CpfDuplicadoException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("CPF duplicado: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro inesperado: " + e.getMessage());
        }
    }

    // Listar por nome ou cpf
    @GetMapping
    public Page<Cliente> listarClientes(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho,
            @RequestParam(defaultValue = "id") String ordenarPor,
            @RequestParam(required = false) String filtro,
            @RequestParam(defaultValue = "nome") String tipoFiltro
    ) {
        if ("cpf".equalsIgnoreCase(tipoFiltro)) {
            return clienteService.listarPaginadoComFiltroPorCpf(pagina, tamanho, ordenarPor, filtro);
        } else {
            return clienteService.listarPaginadoComFiltro(pagina, tamanho, ordenarPor, filtro);
        }
    }


    // Buscar cliente por ID
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> buscarPorId(@PathVariable Long id) {
        return clienteService.buscarPorId(id).map(ResponseEntity::ok).orElse(ResponseEntity.status(404).body(null));
    }

    // Atualizar cliente
    @PutMapping("/{id}")
    public ResponseEntity<Cliente> atualizar(@PathVariable Long id, @RequestBody @Valid Cliente clienteAtualizado) {
        try {
            Cliente atualizado = clienteService.atualizar(id, clienteAtualizado);
            return ResponseEntity.ok(atualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Deletar cliente
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCliente(@PathVariable Long id) {
        try {
            clienteService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }


    // Buscar por nome
    @GetMapping("/buscar")
    public List<Cliente> buscarPorNome(@RequestParam String nome) {
        return clienteService.buscarPorNome(nome);
    }
}