package com.oficina.backend.controller;

import com.oficina.backend.DTO.FinalizarServicoDTO;
import com.oficina.backend.DTO.ServicoFinalizadoDTO;
import com.oficina.backend.model.ServicoFinalizado;
import com.oficina.backend.repository.ServicoFinalizadoRepository;
import com.oficina.backend.service.ServicoFinalizadoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/servicos-finalizados")
@CrossOrigin(origins = "*")
public class ServicoFinalizadoController {

    @Autowired
    private ServicoFinalizadoRepository servicoFinalizadoRepository;

    @Autowired
    private ServicoFinalizadoService servicoFinalizadoService;

    // Excluir serviço finalizado
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (!servicoFinalizadoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        servicoFinalizadoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    // Finalizar serviço em andamento
    @PutMapping("/{id}/finalizar")
    public ResponseEntity<ServicoFinalizadoDTO> finalizarServico(
            @PathVariable Long id,
            @RequestBody FinalizarServicoDTO dto) {

        try {
            if (dto.getDetalhesFinalizacao() == null) {
                dto.setDetalhesFinalizacao("");
            }

            ServicoFinalizado servicoFinalizado = servicoFinalizadoService.finalizarServico(id, dto);

            if (servicoFinalizado == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            ServicoFinalizadoDTO resposta = new ServicoFinalizadoDTO(servicoFinalizado);
            return ResponseEntity.ok(resposta);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    // Listar serviços finalizados com filtros e paginação
    @GetMapping
    public Page<ServicoFinalizadoDTO> listarServicosFinalizados(
            @RequestParam(required = false) String termo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            @RequestParam(required = false) String periodo,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho) {

        Pageable pageable = PageRequest.of(pagina, tamanho);
        Page<ServicoFinalizado> servicos = servicoFinalizadoService.buscarServicosFinalizados(termo, inicio, fim, periodo, pageable);

        // 🔹 Debug: imprimir unidades usadas
        servicos.getContent().forEach(s -> {
            System.out.println("Servico: " + s.getDescricao());
            if (s.getUnidadesUsadas() != null) {
                s.getUnidadesUsadas().forEach(u -> {
                    System.out.println(" - Produto: " + u.getProduto().getNome() + " | Status: " + u.getStatus());
                });
            } else {
                System.out.println(" - Nenhuma unidade usada");
            }
        });


        return servicos.map(ServicoFinalizadoDTO::new);
    }

    // Atualizar detalhesFinalizacao
    @PatchMapping("/{id}")
    public ResponseEntity<ServicoFinalizadoDTO> atualizarDetalhes(@PathVariable Long id, @RequestBody ServicoFinalizado dadosAtualizados) {
        return servicoFinalizadoRepository.findById(id).map(servico -> {
            servico.setDetalhesFinalizacao(dadosAtualizados.getDetalhesFinalizacao());
            servicoFinalizadoRepository.save(servico);
            return ResponseEntity.ok(new ServicoFinalizadoDTO(servico));
        }).orElse(ResponseEntity.notFound().build());
    }

}
