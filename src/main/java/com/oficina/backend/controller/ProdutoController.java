package com.oficina.backend.controller;

import com.oficina.backend.DTO.ProdutoDTO;
import com.oficina.backend.DTO.ProdutoEstoqueDTO;
import com.oficina.backend.DTO.VendaRequestDTO;
import com.oficina.backend.model.Produto;
import com.oficina.backend.model.UnidadeProduto;
import com.oficina.backend.service.ProdutoService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    // Criar produto
    @PostMapping
    public ResponseEntity<ProdutoDTO> criar(@RequestBody @Valid ProdutoDTO dto) {
        ProdutoDTO salvo = produtoService.salvar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    // Listar produtos com paginação e filtro por nome ou categoria
    @GetMapping
    public ResponseEntity<Page<ProdutoDTO>> listarProdutos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nome") String sortBy,
            @RequestParam(required = false) String filtro,
            @RequestParam(defaultValue = "nome") String tipoFiltro,
            @RequestParam(required = false) String status) {

        Page<ProdutoDTO> produtos = produtoService.listarTodos(page, size, sortBy, filtro, tipoFiltro, status);
        return ResponseEntity.ok(produtos);
    }

    // Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<ProdutoDTO> buscarPorId(@PathVariable Long id) {
        ProdutoDTO dto = produtoService.buscarPorId(id);
        return ResponseEntity.ok(dto);
    }

    // Deletar produto
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        produtoService.excluir(id);
        return ResponseEntity.noContent().build();
    }



    // Tratamento local para EntityNotFoundException
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(EntityNotFoundException ex) {
        Map<String, Object> erro = new HashMap<>();
        erro.put("mensagem", ex.getMessage());
        erro.put("status", 404);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    // Atualizar produto
    @PutMapping("/{id}")
    public ResponseEntity<ProdutoDTO> atualizar(@PathVariable Long id, @RequestBody @Valid ProdutoDTO dto) {
        ProdutoDTO atualizado = produtoService.atualizar(id, dto);
        return ResponseEntity.ok(atualizado);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> atualizarStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body
    ) {
        String statusAtualStr = body.get("statusAtual");
        String novoStatusStr = body.get("novoStatus");

        if (!validarStatus(statusAtualStr) || !validarStatus(novoStatusStr)) {
            return ResponseEntity.badRequest().build();
        }

        UnidadeProduto.StatusUnidade statusAtual = UnidadeProduto.StatusUnidade.valueOf(statusAtualStr.toUpperCase());
        UnidadeProduto.StatusUnidade novoStatus = UnidadeProduto.StatusUnidade.valueOf(novoStatusStr.toUpperCase());

        produtoService.alterarStatus(id, statusAtual, novoStatus);
        return ResponseEntity.ok().build();
    }

    private boolean validarStatus(String status) {
        return status != null && (status.equalsIgnoreCase("disponivel")
                || status.equalsIgnoreCase("vendido")
                || status.equalsIgnoreCase("utilizado"));
    }

    @PutMapping("/{id}/vender")
    public ResponseEntity<String> venderProduto(
            @PathVariable Long id,
            @RequestBody VendaRequestDTO vendaDTO
    ) {
        produtoService.venderProduto(id, vendaDTO.getQuantidadeVendida());
        return ResponseEntity.ok("Venda realizada com sucesso");
    }

    @PatchMapping("/{id}/observacao")
    public ResponseEntity<ProdutoDTO> atualizarObservacao(
            @PathVariable Long id,
            @RequestBody String observacao) {
        Produto produto = produtoService.atualizarObservacao(id, observacao);
        return ResponseEntity.ok(new ProdutoDTO(produto));
    }


    @GetMapping("/estoque")
    public ResponseEntity<Page<ProdutoEstoqueDTO>> listarProdutosComEstoque(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nome") String sortBy,
            @RequestParam(required = false) String status // <-- NOVO
    ) {
        if (status != null && !validarStatus(status)) {
            return ResponseEntity.badRequest().build();
        }
        Page<ProdutoEstoqueDTO> produtosEstoque = produtoService.listarTodosComEstoque(page, size, sortBy, status);
        return ResponseEntity.ok(produtosEstoque);
    }


    @GetMapping("/{id}/estoque")
    public ResponseEntity<ProdutoEstoqueDTO> buscarProdutoEstoquePorId(@PathVariable Long id) {
        ProdutoEstoqueDTO dto = produtoService.buscarProdutoEstoquePorId(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/estoque-vendidos")
    public Page<ProdutoEstoqueDTO> listarVendidosComEstoque(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho,
            @RequestParam(defaultValue = "nome") String sortBy
    ) {
        Pageable pageable = PageRequest.of(pagina, tamanho, Sort.by(sortBy));
        return produtoService.listarVendidosComQuantidade(pageable);
    }

}
