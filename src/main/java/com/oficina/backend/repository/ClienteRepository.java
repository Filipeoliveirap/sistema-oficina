package com.oficina.backend.repository;
import com.oficina.backend.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClienteRepository extends JpaRepository<Cliente, Long>{

    Page<Cliente> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
    List<Cliente> findByNomeContainingIgnoreCase(String nome);
    Optional<Cliente> findByCpf(String cpf);
    Page<Cliente> findByCpfContainingIgnoreCase(String cpf, Pageable pageable);


}
