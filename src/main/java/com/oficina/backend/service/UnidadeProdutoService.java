package com.oficina.backend.service;


import com.oficina.backend.model.UnidadeProduto;
import com.oficina.backend.repository.UnidadeProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UnidadeProdutoService {

    @Autowired
    private UnidadeProdutoRepository unidadeProdutoRepository;

    public UnidadeProduto atualizarObservacao(Long id, String observacao) {
        UnidadeProduto unidade = unidadeProdutoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Unidade não encontrada"));
        unidade.setObservacao(observacao);
        return unidadeProdutoRepository.save(unidade);
    }

}
