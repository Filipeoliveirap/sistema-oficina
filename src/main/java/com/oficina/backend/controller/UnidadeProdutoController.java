package com.oficina.backend.controller;

import com.oficina.backend.DTO.UnidadeProdutoDTO;
import com.oficina.backend.model.UnidadeProduto;
import com.oficina.backend.repository.UnidadeProdutoRepository;
import com.oficina.backend.service.UnidadeProdutoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/unidades")
public class UnidadeProdutoController {

    private static final Logger logger = LoggerFactory.getLogger(UnidadeProdutoController.class);

    @Autowired
    private UnidadeProdutoService unidadeProdutoService;

    @Autowired
    private UnidadeProdutoRepository unidadeProdutoRepository;

    @PatchMapping("/{id}/observacao")
    public ResponseEntity<?> atualizarObservacao(
            @PathVariable Long id,
            @RequestBody Map<String, String> body
    ) {
        String observacao = body.get("observacao");

        try {
            logger.info("Atualizando unidade id={} com observacao={}", id, observacao);

            UnidadeProduto unidade = unidadeProdutoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Unidade não encontrada com id " + id));

            unidade.setObservacao(observacao);
            unidadeProdutoRepository.save(unidade);

            logger.info("Atualização realizada com sucesso para unidade id={}", id);

            return ResponseEntity.ok(new UnidadeProdutoDTO(unidade));
        } catch (Exception e) {
            logger.error("Erro ao atualizar unidade id={}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao atualizar unidade: " + e.getMessage());
        }
    }
}
