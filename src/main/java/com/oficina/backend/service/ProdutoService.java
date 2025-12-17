package com.oficina.backend.service;

import com.oficina.backend.DTO.ProdutoComUnidadesDTO;
import com.oficina.backend.DTO.ProdutoDTO;
import com.oficina.backend.DTO.ProdutoEstoqueDTO;
import com.oficina.backend.DTO.UnidadeProdutoDTO;
import com.oficina.backend.model.Produto;
import com.oficina.backend.model.UnidadeProduto;
import com.oficina.backend.repository.ProdutoRepository;
import com.oficina.backend.repository.UnidadeProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private UnidadeProdutoRepository unidadeProdutoRepository;


    private ProdutoDTO toDTO(Produto produto) {
        ProdutoDTO dto = new ProdutoDTO();
        dto.setId(produto.getId());
        dto.setNome(produto.getNome());

        long qtdDisponivel = unidadeProdutoRepository.countByProdutoIdAndStatus(produto.getId(), UnidadeProduto.StatusUnidade.DISPONIVEL);
        dto.setQuantidade((int) qtdDisponivel);

        dto.setPrecoUnitario(produto.getPrecoUnitario());
        dto.setCategoria(produto.getCategoria());
        dto.setObservacao(produto.getObservacao());
        return dto;
    }
    private ProdutoEstoqueDTO toEstoqueDTOFiltradoPorStatus(Produto produto, UnidadeProduto.StatusUnidade statusFiltro) {
        ProdutoEstoqueDTO dto = new ProdutoEstoqueDTO();
        dto.setId(produto.getId());
        dto.setNome(produto.getNome());
        dto.setCategoria(produto.getCategoria());
        dto.setObservacao(produto.getObservacao());
        dto.setPrecoUnitario(produto.getPrecoUnitario());

        List<UnidadeProduto> unidadesFiltradas = produto.getUnidades().stream()
                .filter(u -> u.getStatus() == statusFiltro)
                .collect(Collectors.toList());

        int qtd = unidadesFiltradas.size();
        dto.setQuantidade(qtd);

        dto.setStatus(statusFiltro.name().toLowerCase());

        // Preencher outros campos, se necessário:
        if (statusFiltro == UnidadeProduto.StatusUnidade.VENDIDO) {
            dto.setQuantidadeVendida(qtd);
            dto.setQuantidadeUtilizada(0);
        } else if (statusFiltro == UnidadeProduto.StatusUnidade.UTILIZADO) {
            dto.setQuantidadeUtilizada(qtd);
            dto.setQuantidadeVendida(0);
        } else {
            dto.setQuantidadeVendida(0);
            dto.setQuantidadeUtilizada(0);
        }

        return dto;
    }



    private Produto toEntity(ProdutoDTO dto) {
        Produto produto = new Produto();
        produto.setId(dto.getId());
        produto.setNome(dto.getNome());
        produto.setPrecoUnitario(dto.getPrecoUnitario());
        produto.setCategoria(dto.getCategoria());
        produto.setObservacao(dto.getObservacao());
        return produto;
    }
    private UnidadeProdutoDTO toUnidadeDTO(UnidadeProduto unidade) {
        UnidadeProdutoDTO dto = new UnidadeProdutoDTO();
        dto.setId(unidade.getId());
        dto.setStatus(unidade.getStatus().name().toLowerCase()); // converte ENUM para string minúscula
        return dto;
    }
    public ProdutoComUnidadesDTO toProdutoComUnidadesDTO(Produto produto) {
        ProdutoComUnidadesDTO dto = new ProdutoComUnidadesDTO();
        dto.setId(produto.getId());
        dto.setNome(produto.getNome());
        dto.setQuantidade(produto.getQuantidade());
        dto.setPrecoUnitario(produto.getPrecoUnitario());
        dto.setCategoria(produto.getCategoria());
        dto.setObservacao(produto.getObservacao());
        dto.setUnidades(produto.getUnidades().stream()
                .map(this::toUnidadeDTO)
                .collect(Collectors.toList()));
        return dto;
    }
    private ProdutoEstoqueDTO toEstoqueDTO(Produto produto) {
        ProdutoEstoqueDTO dto = new ProdutoEstoqueDTO();
        dto.setId(produto.getId());
        dto.setNome(produto.getNome());
        dto.setCategoria(produto.getCategoria());
        dto.setObservacao(produto.getObservacao());
        dto.setPrecoUnitario(produto.getPrecoUnitario());

        int disponiveis = (int) produto.getUnidades().stream()
                .filter(u -> u.getStatus() == UnidadeProduto.StatusUnidade.DISPONIVEL)
                .count();

        int vendidos = (int) produto.getUnidades().stream()
                .filter(u -> u.getStatus() == UnidadeProduto.StatusUnidade.VENDIDO)
                .count();

        int utilizados = (int) produto.getUnidades().stream()
                .filter(u -> u.getStatus() == UnidadeProduto.StatusUnidade.UTILIZADO)
                .count();

        dto.setQuantidade(disponiveis);
        dto.setQuantidadeVendida(vendidos);
        dto.setQuantidadeUtilizada(utilizados);

        boolean temDisponivel = disponiveis > 0;
        boolean temVendido = vendidos > 0;
        boolean temUtilizado = utilizados > 0;

        if (temDisponivel && !temVendido && !temUtilizado) {
            dto.setStatus("disponivel");
        } else if (!temDisponivel && temVendido && !temUtilizado) {
            dto.setStatus("vendido");
        } else if (!temDisponivel && !temVendido && temUtilizado) {
            dto.setStatus("utilizado");
        } else {
            dto.setStatus("misto");
        }

        return dto;
    }



    // Salva um novo produto
    @Transactional
    public ProdutoDTO salvar(ProdutoDTO dto) {
        Produto produto = toEntity(dto);
        produto = produtoRepository.save(produto);

        int quantidade = dto.getQuantidade();

        List<UnidadeProduto> unidades = new ArrayList<>();
        for (int i = 0; i < quantidade; i++) {
            UnidadeProduto unidade = new UnidadeProduto();
            unidade.setProduto(produto);
            unidade.setStatus(UnidadeProduto.StatusUnidade.DISPONIVEL);
            unidades.add(unidade);
        }
        unidadeProdutoRepository.saveAll(unidades);

        atualizarQuantidadeProduto(produto);

        return toDTO(produto);
    }



    @Transactional
    public ProdutoDTO atualizar(Long id, ProdutoDTO dto) {
        Produto produtoExistente = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado com id: " + id));

        produtoExistente.setNome(dto.getNome());
        produtoExistente.setPrecoUnitario(dto.getPrecoUnitario());
        produtoExistente.setCategoria(dto.getCategoria());
        produtoExistente.setObservacao(dto.getObservacao());

        produtoRepository.save(produtoExistente);

        int quantidadeDesejada = dto.getQuantidade();

        long unidadesDisponiveis = unidadeProdutoRepository.countByProdutoIdAndStatus(produtoExistente.getId(), UnidadeProduto.StatusUnidade.DISPONIVEL);

        if (quantidadeDesejada > unidadesDisponiveis) {

            int unidadesParaAdicionar = quantidadeDesejada - (int) unidadesDisponiveis;
            List<UnidadeProduto> novasUnidades = new ArrayList<>();
            for (int i = 0; i < unidadesParaAdicionar; i++) {
                UnidadeProduto unidade = new UnidadeProduto();
                unidade.setProduto(produtoExistente);
                unidade.setStatus(UnidadeProduto.StatusUnidade.DISPONIVEL);
                novasUnidades.add(unidade);
            }
            unidadeProdutoRepository.saveAll(novasUnidades);
        } else if (quantidadeDesejada < unidadesDisponiveis) {
            int unidadesParaRemover = (int) unidadesDisponiveis - quantidadeDesejada;
            List<UnidadeProduto> unidadesParaRemoverList = unidadeProdutoRepository.findByProdutoIdAndStatus(produtoExistente.getId(), UnidadeProduto.StatusUnidade.DISPONIVEL)
                    .stream()
                    .sorted((u1, u2) -> Long.compare(u1.getId(), u2.getId()))
                    .limit(unidadesParaRemover)
                    .toList();
            unidadeProdutoRepository.deleteAll(unidadesParaRemoverList);
        }

        atualizarQuantidadeProduto(produtoExistente);  // <-- CHAMADA AQUI

        return toDTO(produtoExistente);
    }





    private ProdutoDTO converterParaDTO(Produto produto) {
        ProdutoDTO dto = new ProdutoDTO();
        dto.setId(produto.getId());
        dto.setNome(produto.getNome());
        dto.setPrecoUnitario(produto.getPrecoUnitario());
        dto.setCategoria(produto.getCategoria());
        dto.setObservacao(produto.getObservacao());

        int qtdDisponivel = (int) produto.getUnidades().stream()
                .filter(u -> u.getStatus() == UnidadeProduto.StatusUnidade.DISPONIVEL)
                .count();
        dto.setQuantidade(qtdDisponivel);

        return dto;
    }

    // Exclui produto pelo id
    public void excluir(Long id) {
        if (!produtoRepository.existsById(id)) {
            throw new EntityNotFoundException("Produto não encontrado com id: " + id);
        }
        produtoRepository.deleteById(id);
    }

    // Busca produto por id
    public ProdutoDTO buscarPorId(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado com id: " + id));
        return toDTO(produto);
    }

    public Page<ProdutoDTO> listarTodos(int page, int size, String sortBy,
                                        String filtro, String tipoFiltro,
                                        String status) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Produto> produtos;

        boolean temFiltro = filtro != null && !filtro.isEmpty();
        boolean temStatus = status != null && !status.isEmpty();

        UnidadeProduto.StatusUnidade statusEnum = null;
        if (temStatus) {
            try {
                statusEnum = UnidadeProduto.StatusUnidade.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status inválido");
            }
        }

        if (temFiltro && temStatus) {
            // Filtra por nome/categoria E status
            if ("nome".equalsIgnoreCase(tipoFiltro)) {
                produtos = produtoRepository.findByNomeContainingIgnoreCaseAndStatus(filtro, statusEnum, pageable);
            } else if ("categoria".equalsIgnoreCase(tipoFiltro)) {
                produtos = produtoRepository.findByCategoriaContainingIgnoreCaseAndStatus(filtro, statusEnum, pageable);
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo de filtro inválido");
            }
        } else if (temFiltro) {
            // Só filtro por nome ou categoria
            if ("nome".equalsIgnoreCase(tipoFiltro)) {
                produtos = produtoRepository.findByNomeContainingIgnoreCase(filtro, pageable);
            } else if ("categoria".equalsIgnoreCase(tipoFiltro)) {
                produtos = produtoRepository.findByCategoriaContainingIgnoreCase(filtro, pageable);
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo de filtro inválido");
            }
        } else if (temStatus) {
            // Só filtro por status
            produtos = produtoRepository.findByStatus(statusEnum, pageable);
        } else {
            // Sem filtro, lista tudo
            produtos = produtoRepository.findAll(pageable);
        }

        return produtos.map(ProdutoDTO::new);
    }




    // Busca produtos pelo nome (contendo, case insensitive) com paginação
    public Page<ProdutoDTO> buscarPorNome(String nome, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Produto> produtos = produtoRepository.findByNomeContainingIgnoreCase(nome, pageable);
        return produtos.map(this::toDTO);
    }

    // Busca produtos pela categoria (contendo, case insensitive) com paginação
    public Page<ProdutoDTO> buscarPorCategoria(String categoria, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Produto> produtos = produtoRepository.findByCategoriaContainingIgnoreCase(categoria, pageable);
        return produtos.map(this::toDTO);
    }



    @Transactional
    public void alterarStatus(Long produtoId, UnidadeProduto.StatusUnidade statusAtual, UnidadeProduto.StatusUnidade novoStatus) {

        List<UnidadeProduto> unidadesParaAtualizar = unidadeProdutoRepository.findByProdutoIdAndStatus(produtoId, statusAtual);

        if (unidadesParaAtualizar.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não existem unidades com o status atual informado.");
        }

        unidadesParaAtualizar.forEach(unidade -> unidade.setStatus(novoStatus));

        unidadeProdutoRepository.saveAll(unidadesParaAtualizar);
    }

    public int contarDisponiveis(Long produtoId) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        return (int) produto.getUnidades().stream()
                .filter(unidade -> unidade.getStatus() == UnidadeProduto.StatusUnidade.DISPONIVEL)
                .count();
    }

    public int contarVendidos(Long produtoId) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        return (int) produto.getUnidades().stream()
                .filter(unidade -> unidade.getStatus() == UnidadeProduto.StatusUnidade.VENDIDO)
                .count();

    }

    public int contarUtilizados(Long produtoId) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        return (int) produto.getUnidades().stream()
                .filter(unidade -> unidade.getStatus() == UnidadeProduto.StatusUnidade.UTILIZADO)
                .count();
    }


    @Transactional
    public void atualizarStatus(Long id, String novoStatus) {
        UnidadeProduto.StatusUnidade statusEnum;
        try {
            statusEnum = UnidadeProduto.StatusUnidade.valueOf(novoStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status inválido");
        }

        List<UnidadeProduto> unidades = unidadeProdutoRepository.findByProdutoIdAndStatus(id, UnidadeProduto.StatusUnidade.DISPONIVEL);
        if (unidades.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nenhuma unidade disponível para atualizar o status");
        }

        // Atualiza todas unidades disponíveis para o novo status
        unidades.forEach(u -> u.setStatus(statusEnum));
        unidadeProdutoRepository.saveAll(unidades);
    }

    public Produto atualizarObservacao(Long id, String observacao) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        produto.setObservacao(observacao);
        return produtoRepository.save(produto);
    }


    @Transactional
    public void venderProduto(Long produtoId, int quantidadeVendida) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));

        if (quantidadeVendida <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantidade inválida");
        }

        Pageable limit = PageRequest.of(0, quantidadeVendida);
        List<UnidadeProduto> disponiveis = unidadeProdutoRepository
                .findByProdutoIdAndStatusOrderByIdAsc(produtoId, UnidadeProduto.StatusUnidade.DISPONIVEL, limit);

        if (disponiveis.size() < quantidadeVendida) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estoque insuficiente");
        }

        disponiveis.forEach(u -> u.setStatus(UnidadeProduto.StatusUnidade.VENDIDO));
        unidadeProdutoRepository.saveAll(disponiveis);

        atualizarQuantidadeProduto(produto);
    }


    public Page<ProdutoEstoqueDTO> listarTodosComEstoque(int page, int size, String sortBy, String statusFiltro) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Produto> paginaProdutos = produtoRepository.findAll(pageable);

        List<ProdutoEstoqueDTO> resultado = new ArrayList<>();

        for (Produto produto : paginaProdutos.getContent()) {
            // Filtra pelo status, se tiver
            List<UnidadeProduto> unidadesFiltradas = produto.getUnidades().stream()
                    .filter(u -> statusFiltro == null || statusFiltro.isEmpty()
                            || u.getStatus().name().equalsIgnoreCase(statusFiltro))
                    .sorted((u1, u2) -> Long.compare(u1.getId(), u2.getId()))
                    .collect(Collectors.toList());

            if (unidadesFiltradas.isEmpty()) continue;

            // Agrupar por status consecutivo
            UnidadeProduto.StatusUnidade statusAtual = unidadesFiltradas.get(0).getStatus();
            List<UnidadeProduto> grupo = new ArrayList<>();

            for (UnidadeProduto unidade : unidadesFiltradas) {
                if (unidade.getStatus() == statusAtual) {
                    grupo.add(unidade);
                } else {
                    // cria DTO para o grupo atual
                    resultado.add(criarDTOProdutoPorGrupo(produto, grupo, statusAtual));
                    // inicia novo grupo
                    grupo = new ArrayList<>();
                    grupo.add(unidade);
                    statusAtual = unidade.getStatus();
                }
            }
            // adiciona o último grupo
            if (!grupo.isEmpty()) {
                resultado.add(criarDTOProdutoPorGrupo(produto, grupo, statusAtual));
            }
        }

        return new PageImpl<>(resultado, pageable, resultado.size());
    }

    // método auxiliar para criar DTO de um grupo de unidades
    private ProdutoEstoqueDTO criarDTOProdutoPorGrupo(Produto produto, List<UnidadeProduto> grupo, UnidadeProduto.StatusUnidade status) {
        ProdutoEstoqueDTO dto = new ProdutoEstoqueDTO();
        dto.setId(produto.getId());
        dto.setNome(produto.getNome());
        dto.setCategoria(produto.getCategoria());
        dto.setObservacao(produto.getObservacao());
        dto.setPrecoUnitario(produto.getPrecoUnitario());
        dto.setStatus(status.name().toLowerCase());

        // Quantidade do grupo
        dto.setQuantidade(grupo.size());

        if (status == UnidadeProduto.StatusUnidade.VENDIDO) {
            dto.setQuantidadeVendida(grupo.size());
            dto.setQuantidadeUtilizada(0);
        } else if (status == UnidadeProduto.StatusUnidade.UTILIZADO) {
            dto.setQuantidadeUtilizada(grupo.size());
            dto.setQuantidadeVendida(0);
        } else {
            dto.setQuantidadeVendida(0);
            dto.setQuantidadeUtilizada(0);
        }

        return dto;
    }





    public Page<ProdutoEstoqueDTO> listarVendidos(Pageable pageable) {
        Page<Produto> produtos = produtoRepository.findAll(pageable);

        List<ProdutoEstoqueDTO> filtrados = produtos.stream()
                .filter(p -> p.getUnidades().stream()
                        .anyMatch(u -> u.getStatus() == UnidadeProduto.StatusUnidade.VENDIDO))
                .map(p -> toEstoqueDTOFiltradoPorStatus(p, UnidadeProduto.StatusUnidade.VENDIDO))
                .collect(Collectors.toList());

        return new PageImpl<>(filtrados, pageable, filtrados.size());
    }

    private void atualizarQuantidadeProduto(Produto produto) {
        int quantidade = (int) unidadeProdutoRepository.countByProdutoIdAndStatus(produto.getId(), UnidadeProduto.StatusUnidade.DISPONIVEL);
        produto.setQuantidade(quantidade);
        produtoRepository.save(produto);
    }
    public ProdutoEstoqueDTO buscarProdutoEstoquePorId(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado com id: " + id));
        return toEstoqueDTO(produto);
    }

    public Page<ProdutoEstoqueDTO> listarVendidosComQuantidade(Pageable pageable) {
        Page<Produto> produtos = produtoRepository.findAll(pageable);

        List<ProdutoEstoqueDTO> vendidos = produtos.stream()
                .filter(p -> p.getUnidades().stream()
                        .anyMatch(u -> u.getStatus() == UnidadeProduto.StatusUnidade.VENDIDO))
                .map(p -> toEstoqueDTOFiltradoPorStatus(p, UnidadeProduto.StatusUnidade.VENDIDO))
                .collect(Collectors.toList());

        return new PageImpl<>(vendidos, pageable, vendidos.size());
    }




}
