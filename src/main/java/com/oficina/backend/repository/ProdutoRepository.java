package com.oficina.backend.repository;

import com.oficina.backend.model.Produto;
import com.oficina.backend.model.UnidadeProduto;
import com.oficina.backend.model.UnidadeProduto.StatusUnidade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    @Query("SELECT DISTINCT p FROM Produto p JOIN p.unidades u WHERE u.status = :status")
    Page<Produto> findByStatus(@Param("status") UnidadeProduto.StatusUnidade status, Pageable pageable);




    Page<Produto> findByNomeContainingIgnoreCase(String nome, Pageable pageable);


    Page<Produto> findByCategoriaContainingIgnoreCase(String categoria, Pageable pageable);


    @Query("SELECT DISTINCT p FROM Produto p JOIN p.unidades u WHERE LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%')) AND u.status = :status")
    Page<Produto> findByNomeContainingIgnoreCaseAndStatus(@Param("nome") String nome, @Param("status") StatusUnidade status, Pageable pageable);


    @Query("SELECT DISTINCT p FROM Produto p JOIN p.unidades u WHERE LOWER(p.categoria) LIKE LOWER(CONCAT('%', :categoria, '%')) AND u.status = :status")
    Page<Produto> findByCategoriaContainingIgnoreCaseAndStatus(@Param("categoria") String categoria, @Param("status") StatusUnidade status, Pageable pageable);

}
