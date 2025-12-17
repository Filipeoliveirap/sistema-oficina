package com.oficina.backend.controller;

import com.oficina.backend.DTO.ServicoRequestDTO;
import com.oficina.backend.DTO.ServicoResponseDTO;
import com.oficina.backend.mapper.ServicoMapper;
import com.oficina.backend.model.Servico;
import com.oficina.backend.service.ServicoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/servicos")
@CrossOrigin(origins = "*")
public class ServicoController {

    @Autowired
    private ServicoService servicoService;

    // Criar serviço
    @PostMapping
    public ResponseEntity<?> criarServico(@RequestBody @Valid ServicoRequestDTO dto) {
        try {
            Servico servico = servicoService.salvarServico(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(servico);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"erro\":\"" + e.getMessage() + "\"}");
        }
    }

    // Atualizar serviço
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarServico(@PathVariable Long id, @RequestBody @Valid ServicoRequestDTO dto) {
        try {
            Servico atualizado = servicoService.atualizarComDTO(id, dto);
            return ResponseEntity.ok(atualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"erro\":\"" + e.getMessage() + "\"}");
        }
    }

    // Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<ServicoResponseDTO> buscarPorId(@PathVariable Long id) {
        Servico servico = servicoService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));

        ServicoResponseDTO dto = ServicoMapper.toResponseDTO(servico);
        return ResponseEntity.ok(dto);
    }


    // Listar todos os serviços
    @GetMapping
    public ResponseEntity<List<ServicoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(servicoService.listarServicos());
    }


    // Deletar serviço
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        servicoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    // Buscar por intervalo de datas
    @GetMapping("/data")
    public ResponseEntity<List<Servico>> buscarPorData(
            @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate fim) {
        return ResponseEntity.ok(servicoService.buscarPorPeriodo(inicio, fim));
    }

    // Buscar por descrição ou CPF do cliente
    @GetMapping("/buscar-tudo")
    public ResponseEntity<List<Servico>> buscarPorDescricaoOuCpf(@RequestParam String termo) {
        return ResponseEntity.ok(servicoService.buscarPorDescricaoOuCpf(termo));
    }

    // Histórico últimos serviços (limit 10)
    @GetMapping("/historico")
    public ResponseEntity<List<Servico>> getHistoricoUltimosServicos() {
        return ResponseEntity.ok(servicoService.getUltimosServicos(10));
    }
}
