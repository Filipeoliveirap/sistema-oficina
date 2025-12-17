package com.oficina.backend.repository;
import com.oficina.backend.model.Servico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.time.LocalDate;
import java.util.List;

public interface ServicoRepository extends JpaRepository<Servico, Long>{
    boolean existsByClienteId(Long clienteId);
    List<Servico> findByDescricaoContainingIgnoreCaseOrClienteCpfContaining(String descricao, String cpf);
    List<Servico> findByClienteId(Long clienteId);
    List<Servico> findByCliente_NomeContainingIgnoreCase(String nome);
    List<Servico> findByDescricaoContainingIgnoreCase(String descricao);
    List<Servico> findByClienteCpfContaining(String cpf);
    List<Servico> findByDataBetween(LocalDate inicio, LocalDate fim);
    @Query("SELECT s FROM Servico s ORDER BY s.data DESC")
    List<Servico> findUltimosServicos();


}
