package com.oficina.backend.service;

import com.oficina.backend.DTO.*;
import com.oficina.backend.model.*;
import com.oficina.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ServicoService {

    @Autowired
    private ServicoRepository servicoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private UnidadeProdutoRepository unidadeProdutoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private VeiculoRepository veiculoRepository;


    // Listar todos os serviços
    public List<ServicoResponseDTO> listarServicos() {
        return servicoRepository.findAll().stream()
                .map(servico -> {
                    // Mapear produtos usados
                    List<ProdutoUsadoResponseDTO> produtosDTO = new ArrayList<>();
                    if (servico.getProdutos() != null && !servico.getProdutos().isEmpty()) {
                        for (Produto produto : servico.getProdutos()) {
                            // Contar quantas unidades do produto foram usadas
                            long quantidadeUsada = servico.getUnidadesUsadas().stream()
                                    .filter(u -> u.getProduto().getId().equals(produto.getId()))
                                    .count();

                            produtosDTO.add(new ProdutoUsadoResponseDTO(
                                    produto.getId(),
                                    produto.getNome(),
                                    (int) quantidadeUsada
                            ));
                        }
                    }

                    return new ServicoResponseDTO(
                            servico.getId(),
                            servico.getDescricao(),
                            servico.getPreco(),
                            servico.getData(),
                            new ClienteResumidoDTO(servico.getCliente().getId(), servico.getCliente().getNome(), servico.getCliente().getCpf()),
                            servico.getVeiculo() != null
                                    ? new VeiculoResumidoDTO(servico.getVeiculo().getId(), servico.getVeiculo().getModelo(), servico.getVeiculo().getPlaca())
                                    : null,
                            produtosDTO
                    );
                }).toList();
    }



    // Buscar por ID
    public Optional<Servico> buscarPorId(Long id) {
        return servicoRepository.findById(id);
    }

    // Deletar serviço
    public void deletar(Long id) {
        servicoRepository.deleteById(id);
    }

    // Buscar por nome do cliente
    public List<Servico> buscarPorNomeDoCliente(String nome) {
        return servicoRepository.findByCliente_NomeContainingIgnoreCase(nome);
    }

    // Buscar por período
    public List<Servico> buscarPorPeriodo(LocalDate inicio, LocalDate fim) {
        return servicoRepository.findByDataBetween(inicio, fim);
    }

    // Atualizar serviço
    public Servico atualizar(Long id, Servico servicoAtualizado) {
        return servicoRepository.findById(id).map(servicoExistente -> {

            // Repor estoque dos produtos antigos
            servicoExistente.getProdutos().forEach(produtoUsado -> {
                Produto produto = produtoRepository.findById(produtoUsado.getId()).orElseThrow();
                produto.setQuantidade(produto.getQuantidade() + produtoUsado.getQuantidade());
                produtoRepository.save(produto);
            });

            // Validar novos produtos
            servicoAtualizado.getProdutos().forEach(produtoNovo -> {
                Produto produto = produtoRepository.findById(produtoNovo.getId())
                        .orElseThrow(() -> new RuntimeException("Produto com id " + produtoNovo.getId() + " não encontrado"));
                if (produto.getQuantidade() < produtoNovo.getQuantidade()) {
                    throw new RuntimeException("Estoque insuficiente para o produto: " + produto.getNome());
                }
            });

            // Atualizar estoque com novos produtos
            servicoAtualizado.getProdutos().forEach(produtoNovo -> {
                Produto produto = produtoRepository.findById(produtoNovo.getId()).get();
                produto.setQuantidade(produto.getQuantidade() - produtoNovo.getQuantidade());
                produtoRepository.save(produto);
            });

            // Atualizar dados do serviço
            servicoExistente.setData(servicoAtualizado.getData());
            servicoExistente.setDescricao(servicoAtualizado.getDescricao());
            servicoExistente.setPreco(servicoAtualizado.getPreco());
            servicoExistente.setCliente(servicoAtualizado.getCliente());
            servicoExistente.setProdutos(servicoAtualizado.getProdutos());

            // Atualizar veículo
            if (servicoAtualizado.getVeiculo() != null && servicoAtualizado.getVeiculo().getId() != null) {
                Veiculo veiculo = veiculoRepository.findById(servicoAtualizado.getVeiculo().getId())
                        .orElseThrow(() -> new RuntimeException("Veículo não encontrado"));
                servicoExistente.setVeiculo(veiculo);
            } else {
                servicoExistente.setVeiculo(null);
            }

            return servicoRepository.save(servicoExistente);

        }).orElseThrow(() -> new RuntimeException("Serviço com id " + id + " não encontrado"));
    }

    // Salvar serviço via DTO
    @Transactional
    public Servico salvarServico(ServicoRequestDTO dto) {
        if (dto.getCliente() == null || dto.getCliente().getId() == null) {
            throw new RuntimeException("Cliente é obrigatório");
        }

        Cliente cliente = clienteRepository.findById(dto.getCliente().getId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        Servico servico = new Servico();
        servico.setDescricao(dto.getDescricao());
        servico.setPreco(dto.getPreco());
        servico.setData(dto.getData().atStartOfDay());
        servico.setCliente(cliente);

        // Veículo opcional
        if (dto.getVeiculo() != null && dto.getVeiculo().getId() != null) {
            Veiculo veiculo = veiculoRepository.findById(dto.getVeiculo().getId())
                    .orElseThrow(() -> new RuntimeException("Veículo não encontrado"));
            servico.setVeiculo(veiculo);
        }

        // Processar produtos usados
        List<UnidadeProduto> unidadesUsadas = new ArrayList<>();
        List<Produto> produtosUsados = new ArrayList<>();

        if (dto.getProdutosUsados() != null) {
            for (ProdutoServicoDTO pDto : dto.getProdutosUsados()) {
                Produto produto = produtoRepository.findById(pDto.getId())
                        .orElseThrow(() -> new RuntimeException("Produto não encontrado: id " + pDto.getId()));

                // Buscar unidades disponíveis
                List<UnidadeProduto> disponiveis = unidadeProdutoRepository
                        .findByProdutoIdAndStatusOrderByIdAsc(
                                produto.getId(),
                                UnidadeProduto.StatusUnidade.DISPONIVEL,
                                PageRequest.of(0, pDto.getQuantidade())
                        );

                if (disponiveis.size() < pDto.getQuantidade()) {
                    throw new RuntimeException("Estoque insuficiente para produto: " + produto.getNome());
                }

                // Marcar unidades como UTILIZADO
                disponiveis.forEach(u -> u.setStatus(UnidadeProduto.StatusUnidade.UTILIZADO));
                unidadeProdutoRepository.saveAll(disponiveis);

                unidadesUsadas.addAll(disponiveis);
                produtosUsados.add(produto);
            }
        }

        servico.setUnidadesUsadas(unidadesUsadas);
        servico.setProdutos(produtosUsados);

        return servicoRepository.save(servico);
    }

    // Atualizar serviço via DTO
    @Transactional
    public Servico atualizarComDTO(Long id, ServicoRequestDTO dto) {
        Servico servicoExistente = servicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serviço com id " + id + " não encontrado"));

        // Repor unidades antigas
        servicoExistente.getUnidadesUsadas()
                .forEach(u -> u.setStatus(UnidadeProduto.StatusUnidade.DISPONIVEL));
        unidadeProdutoRepository.saveAll(servicoExistente.getUnidadesUsadas());

        servicoExistente.setDescricao(dto.getDescricao());
        servicoExistente.setPreco(dto.getPreco());
        servicoExistente.setData(dto.getData().atStartOfDay());

        if (dto.getCliente() == null || dto.getCliente().getId() == null) {
            throw new RuntimeException("Cliente é obrigatório");
        }
        Cliente cliente = clienteRepository.findById(dto.getCliente().getId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        servicoExistente.setCliente(cliente);

        if (dto.getVeiculo() != null && dto.getVeiculo().getId() != null) {
            Veiculo veiculo = veiculoRepository.findById(dto.getVeiculo().getId())
                    .orElseThrow(() -> new RuntimeException("Veículo não encontrado"));
            servicoExistente.setVeiculo(veiculo);
        } else {
            servicoExistente.setVeiculo(null);
        }

        // Alocar novas unidades e produtos
        List<UnidadeProduto> novasUnidades = new ArrayList<>();
        List<Produto> novosProdutos = new ArrayList<>();

        if (dto.getProdutosUsados() != null) {
            for (ProdutoServicoDTO pDto : dto.getProdutosUsados()) {
                Produto produto = produtoRepository.findById(pDto.getId())
                        .orElseThrow(() -> new RuntimeException("Produto não encontrado: id " + pDto.getId()));

                List<UnidadeProduto> disponiveis = unidadeProdutoRepository
                        .findByProdutoIdAndStatusOrderByIdAsc(
                                produto.getId(),
                                UnidadeProduto.StatusUnidade.DISPONIVEL,
                                PageRequest.of(0, pDto.getQuantidade())
                        );

                if (disponiveis.size() < pDto.getQuantidade()) {
                    throw new RuntimeException("Estoque insuficiente para produto: " + produto.getNome());
                }

                disponiveis.forEach(u -> u.setStatus(UnidadeProduto.StatusUnidade.UTILIZADO));
                unidadeProdutoRepository.saveAll(disponiveis);

                novasUnidades.addAll(disponiveis);
                novosProdutos.add(produto);
            }
        }

        servicoExistente.setUnidadesUsadas(novasUnidades);
        servicoExistente.setProdutos(novosProdutos);

        return servicoRepository.save(servicoExistente);
    }


    // Buscar por descrição ou CPF do cliente
    public List<Servico> buscarPorDescricaoOuCpf(String termo) {
        return servicoRepository.findByDescricaoContainingIgnoreCaseOrClienteCpfContaining(termo, termo);
    }

    // Buscar últimos serviços
    public List<Servico> getUltimosServicos(int limite) {
        return servicoRepository.findUltimosServicos().stream()
                .limit(limite)
                .collect(Collectors.toList());
    }
}
