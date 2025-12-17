package com.oficina.backend.controller;

import com.oficina.backend.DTO.VeiculoResumidoDTO;
import com.oficina.backend.model.Cliente;
import com.oficina.backend.model.Veiculo;
import com.oficina.backend.repository.ClienteRepository;
import com.oficina.backend.repository.VeiculoRepository;
import com.oficina.backend.service.VeiculoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/veiculos")
@CrossOrigin(origins = "*")
public class VeiculoController {

    @Autowired
    private VeiculoService veiculoService;

    @Autowired
    private VeiculoRepository veiculoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @PostMapping
    public ResponseEntity<Veiculo> criarVeiculo(@RequestBody @Valid Veiculo veiculo) {
        Cliente cliente = clienteRepository.findById(veiculo.getCliente().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cliente não encontrado"));

        veiculo.setCliente(cliente);
        veiculo.setCpfCliente(cliente.getCpf());

        Veiculo salvo = veiculoRepository.save(veiculo);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    // Listar veículos com paginação e filtro opcional
    @GetMapping
    public ResponseEntity<Page<Veiculo>> listarVeiculos(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho,
            @RequestParam(defaultValue = "") String filtro,
            @RequestParam(defaultValue = "modelo") String tipoFiltro
    ) {
        Pageable pageable = PageRequest.of(pagina, tamanho, Sort.by("modelo").ascending());

        Page<Veiculo> resultado;

        if (filtro.isEmpty()) {
            resultado = veiculoRepository.findAll(pageable);
        } else {
            switch (tipoFiltro.toLowerCase()) {
                case "placa":
                    resultado = veiculoRepository.findByPlacaContainingIgnoreCase(filtro, pageable);
                    break;
                case "cpfcliente":
                    resultado = veiculoRepository.findByClienteCpfContaining(filtro, pageable);
                    break;
                default: // modelo
                    resultado = veiculoRepository.findByModeloContainingIgnoreCase(filtro, pageable);
            }
        }

        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Veiculo> buscarPorId(@PathVariable Long id) {
        Optional<Veiculo> veiculo = veiculoService.buscarPorId(id);
        return veiculo.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Veiculo> atualizar(@PathVariable Long id, @RequestBody @Valid Veiculo veiculoAtualizado) {
        try {
            Veiculo atualizado = veiculoService.atualizar(id, veiculoAtualizado);
            return ResponseEntity.ok(atualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        try {
            veiculoService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @GetMapping("/resumido")
    public ResponseEntity<List<VeiculoResumidoDTO>> listarVeiculosResumidos() {
        List<Veiculo> veiculos = veiculoRepository.findAll();
        List<VeiculoResumidoDTO> dtos = veiculos.stream()
                .map(VeiculoResumidoDTO::new)
                .toList();
        return ResponseEntity.ok(dtos);
    }


    // Buscar veículos de um cliente pelo CPF (sem paginação)
    @GetMapping("/buscar/cpf")
    public ResponseEntity<List<Veiculo>> buscarPorCpf(@RequestParam String cpf) {
        List<Veiculo> veiculos = veiculoService.buscarPorCpfCliente(cpf);
        return ResponseEntity.ok(veiculos);
    }

}
