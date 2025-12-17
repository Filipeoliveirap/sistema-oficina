package com.oficina.backend.repository;

import com.oficina.backend.model.ServicoFinalizado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ServicoFinalizadoRepository extends JpaRepository<ServicoFinalizado, Long> {


    @Query("SELECT s FROM ServicoFinalizado s JOIN FETCH s.cliente ORDER BY s.dataFinalizacao DESC")
    List<ServicoFinalizado> findTop5WithCliente();

    @Query("SELECT s FROM ServicoFinalizado s " +
            "LEFT JOIN FETCH s.unidadesUsadas u " +
            "LEFT JOIN FETCH u.produto " +
            "WHERE (:termo IS NULL OR LOWER(s.descricao) LIKE LOWER(CONCAT('%', :termo, '%')) OR s.cpfCliente LIKE CONCAT('%', :termo, '%')) " +
            "AND (:dataInicio IS NULL OR s.dataInicio >= :dataInicio) " +
            "AND (:dataFim IS NULL OR s.dataFinalizacao <= :dataFim)")
    Page<ServicoFinalizado> buscarComFiltros(@Param("termo") String termo,
                                             @Param("dataInicio") LocalDateTime dataInicio,
                                             @Param("dataFim") LocalDateTime dataFim,
                                             Pageable pageable);


    List<ServicoFinalizado> findTop5ByOrderByDataFinalizacaoDesc();

    @Query(value = "SELECT strftime('%Y-%m', data_finalizacao) as mes, COUNT(*) " +
            "FROM servico_finalizado " +
            "GROUP BY mes " +
            "ORDER BY mes", nativeQuery = true)
    List<Object[]> contarServicosPorMes();
    Optional<ServicoFinalizado> findTopByServicoOriginalIdOrderByDataFinalizacaoDesc(Long servicoId);

    @Query(value = "SELECT strftime('%Y-%m', data_finalizacao), COUNT(*) " +
            "FROM servico_finalizado " +
            "WHERE data_finalizacao >= :limite " +
            "GROUP BY strftime('%Y-%m', data_finalizacao) " +
            "ORDER BY strftime('%Y-%m', data_finalizacao)", nativeQuery = true)
    List<Object[]> contarServicosPorMesUltimos4Meses(@Param("limite") LocalDateTime limite);
}
