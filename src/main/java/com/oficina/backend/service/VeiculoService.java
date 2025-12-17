package com.oficina.backend.service;

import com.oficina.backend.model.Cliente;
import com.oficina.backend.model.Veiculo;
import com.oficina.backend.repository.ClienteRepository;
import com.oficina.backend.repository.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VeiculoService {

    @Autowired
    private VeiculoRepository veiculoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    public Veiculo salvar(Veiculo veiculo) {

        Cliente cliente = clienteRepository.findById(veiculo.getCliente().getId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        veiculo.setCliente(cliente);
        veiculo.setCpfCliente(cliente.getCpf());
        return veiculoRepository.save(veiculo);
    }


    public List<Veiculo> listarTodos() {
        return veiculoRepository.findAll();
    }


    public Optional<Veiculo> buscarPorId(Long id) {
        return veiculoRepository.findById(id);
    }


    public List<Veiculo> buscarPorCpfCliente(String cpf) {
        return veiculoRepository.findByCpfClienteContaining(cpf);
    }


    public List<Veiculo> buscarPorModelo(String modelo) {
        return veiculoRepository.findByModeloContainingIgnoreCase(modelo);
    }

    public List<Veiculo> buscarPorPlaca(String placa) {
        return veiculoRepository.findByPlacaContainingIgnoreCase(placa);
    }

    public List<Veiculo> buscarPorNomeCliente(String nome) {
        return veiculoRepository.findByClienteNomeContainingIgnoreCase(nome);
    }

    public Veiculo atualizar(Long id, Veiculo veiculoAtualizado) {
        return veiculoRepository.findById(id).map(veiculoExistente -> {

            veiculoExistente.setModelo(veiculoAtualizado.getModelo());
            veiculoExistente.setPlaca(veiculoAtualizado.getPlaca());
            veiculoExistente.setAno(veiculoAtualizado.getAno());
            veiculoExistente.setCor(veiculoAtualizado.getCor());

            Cliente cliente = clienteRepository.findById(veiculoAtualizado.getCliente().getId())
                    .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
            veiculoExistente.setCliente(cliente);
            veiculoExistente.setCpfCliente(cliente.getCpf());

            return veiculoRepository.save(veiculoExistente);
        }).orElseThrow(() -> new RuntimeException("Veículo com id " + id + " não encontrado"));
    }

    public void deletar(Long id) {
        if (!veiculoRepository.existsById(id)) {
            throw new RuntimeException("Veículo não encontrado");
        }
        veiculoRepository.deleteById(id);
    }
}
