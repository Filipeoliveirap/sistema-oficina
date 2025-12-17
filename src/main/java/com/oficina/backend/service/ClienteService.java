package com.oficina.backend.service;
import com.oficina.backend.exception.CpfDuplicadoException;
import com.oficina.backend.model.Cliente;
import com.oficina.backend.repository.ClienteRepository;
import com.oficina.backend.repository.ServicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {
    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ServicoRepository servicoRepository;

    public ClienteService(ClienteRepository clienteRepository){
        this.clienteRepository = clienteRepository;
    }

    //lista todos os clientes
    public List<Cliente> listarTodos(){
        return clienteRepository.findAll();
    }

    //busca cliente por id
    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);

    }

    //cria um novo cliente com verificação de duplicidade
    public Cliente salvar(Cliente cliente) {
        // Verifica se o cliente já existe com o CPF
        Optional<Cliente> clienteExistente = clienteRepository.findByCpf(cliente.getCpf());

        // Verifica se já existe um cliente com o CPF, mas o próprio cliente não é o encontrado
        if (clienteExistente.isPresent() && !clienteExistente.get().getId().equals(cliente.getId())) {
            throw new CpfDuplicadoException(cliente.getCpf());
        }

        // Salva o cliente (novo ou atualizado)
        return clienteRepository.save(cliente);
    }

    //deleta cliente
    public void deletar(Long clienteId) {
        boolean possuiServicos = servicoRepository.existsByClienteId(clienteId);
        if (possuiServicos) {
            throw new RuntimeException("Este cliente possui serviços cadastrados e não pode ser excluído.");
        }

        clienteRepository.deleteById(clienteId);
    }

    //atualiza cliente
    public Cliente atualizar(Long id, Cliente clienteAtualizado){
        return clienteRepository.findById(id).map(cliente -> {
            boolean cpfExistente = clienteRepository.findAll().stream()
                    .anyMatch(c -> !c.getId().equals(id) && c.getCpf().equals(clienteAtualizado.getCpf()));
            if (cpfExistente) {
                throw new CpfDuplicadoException(clienteAtualizado.getCpf());
            }
            cliente.setNome(clienteAtualizado.getNome());
            cliente.setCpf(clienteAtualizado.getCpf());
            cliente.setTelefone(clienteAtualizado.getTelefone());
            cliente.setEmail(clienteAtualizado.getEmail());
            cliente.setEndereco(clienteAtualizado.getEndereco());
            return clienteRepository.save(cliente);
        }).orElseThrow(() -> new RuntimeException("Cliente com ID " + id + " não encontrado"));
    }

    //buscar por nome
    public List<Cliente> buscarPorNome(String nome){
        return clienteRepository.findByNomeContainingIgnoreCase(nome);
    }
    public Page<Cliente> listarPaginado(int pagina, int tamanho, String campoOrdenacao) {
        PageRequest pageable = PageRequest.of(pagina, tamanho, Sort.by(campoOrdenacao));
        return clienteRepository.findAll(pageable);
    }

    public Page<Cliente> listarPaginadoComFiltro(int pagina, int tamanho, String campoOrdenacao, String filtro) {
        PageRequest pageable = PageRequest.of(pagina, tamanho, Sort.by(campoOrdenacao));

        if (filtro != null && !filtro.isEmpty()) {
            return clienteRepository.findByNomeContainingIgnoreCase(filtro, pageable);
        } else {
            return clienteRepository.findAll(pageable);
        }
    }

    public Page<Cliente> listarPaginadoComFiltroPorCpf(int pagina, int tamanho, String campoOrdenacao, String filtro) {
        PageRequest pageable = PageRequest.of(pagina, tamanho, Sort.by(campoOrdenacao));

        if (filtro != null && !filtro.isEmpty()) {
            return clienteRepository.findByCpfContainingIgnoreCase(filtro, pageable);
        } else {
            return clienteRepository.findAll(pageable);
        }
    }
}
