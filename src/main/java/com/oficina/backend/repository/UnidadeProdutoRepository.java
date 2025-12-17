package com.oficina.backend.repository;

import com.oficina.backend.model.UnidadeProduto;
import com.oficina.backend.model.UnidadeProduto.StatusUnidade;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnidadeProdutoRepository extends JpaRepository<UnidadeProduto, Long> {

    List<UnidadeProduto> findByProdutoIdAndStatus(Long produtoId, StatusUnidade status);

    long countByProdutoIdAndStatus(Long produtoId, StatusUnidade status);

    List<UnidadeProduto> findByProdutoIdAndStatusOrderByIdAsc(Long produtoId, StatusUnidade status, Pageable pageable);

}
