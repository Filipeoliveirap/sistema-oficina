let paginaAtual = 0;
const tamanhoPagina = 10;

function editarProduto(id) {
  window.location.href = `estoqueCadastro.html?id=${id}`;
}

function converterStatusParaEnum(status) {
  switch (status.toLowerCase()) {
    case "disponivel":
      return "DISPONIVEL";
    case "vendido":
      return "VENDIDO";
    case "utilizado":
      return "UTILIZADO";
    default:
      return status.toUpperCase();
  }
}

async function atualizarStatusProduto(id, novoStatus) {
  try {
    const statusEnum = converterStatusParaEnum(novoStatus);

    const linha = document.querySelector(`tr[data-id="${id}"]`);
    const select = linha.querySelector("select");
    const statusAtualStr =
      select.getAttribute("data-status-atual") || novoStatus;

    const response = await fetch(
      `http://localhost:8080/produtos/${id}/status`,
      {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          statusAtual: statusAtualStr.toUpperCase(),
          novoStatus: statusEnum,
        }),
      }
    );

    if (!response.ok) {
      throw new Error("Erro ao atualizar status do produto");
    }

    await alertaSucesso("Status atualizado com sucesso!");

    carregarProdutos(
      paginaAtual,
      filtroAtual,
      tipoFiltroAtual,
      statusFiltroSelect.value
    );
  } catch (error) {
    console.error("Erro:", error.message);
    await alertaErro("Erro ao atualizar status", error.message);
  }
}

async function excluirProduto(id) {
  const confirmado = await confirmarAcao(
    "Tem certeza que deseja excluir este produto?"
  );
  if (!confirmado) return;

  try {
    const response = await fetch(`http://localhost:8080/produtos/${id}`, {
      method: "DELETE",
    });

    if (!response.ok) throw new Error("Erro ao excluir o produto");

    const linha = document.querySelector(`tr[data-id="${id}"]`);
    if (linha) linha.remove();

    await alertaSucesso("Produto excluído com sucesso!");
  } catch (error) {
    console.error("Erro:", error.message);
    await alertaErro("Erro ao excluir produto", error.message);
  }
}

document.addEventListener("DOMContentLoaded", () => {
  const tbody = document.querySelector("#tabela-produtos tbody");
  const inputBusca = document.getElementById("pesquisar-estoque");
  const btnBusca = document.getElementById("btn-buscar-estoque");
  const tipoFiltroSelect = document.getElementById("tipo-filtro-estoque");
  const statusFiltroSelect = document.getElementById("status-filtro");

  let filtroAtual = "";
  let tipoFiltroAtual = "nome";

  async function carregarProdutos(
    pagina,
    filtro = "",
    tipoFiltro = "nome",
    status = ""
  ) {
    try {
      let url;

      if (status.toLowerCase() === "vendido") {
        url = `http://localhost:8080/produtos/estoque-vendidos?pagina=${pagina}&tamanho=${tamanhoPagina}&sortBy=nome`;
      } else {
        const params = new URLSearchParams();
        params.append("pagina", pagina);
        params.append("tamanho", tamanhoPagina);
        params.append("sortBy", "nome");
        params.append("tipoFiltro", tipoFiltro);
        if (filtro) params.append("filtro", filtro);
        const statusNormalizado = status?.toLowerCase();

        if (statusNormalizado && statusNormalizado !== "todos") {
          params.append("status", statusNormalizado);
        }

        url = `http://localhost:8080/produtos/estoque?${params.toString()}`;
      }

      console.log("URL da requisição:", url);

      const response = await fetch(url);
      if (!response.ok) throw new Error("Erro ao buscar produtos");

      const dados = await response.json();
      console.log("Dados recebidos:", dados);

      tbody.innerHTML = "";

      if (!dados.content || dados.content.length === 0) {
        tbody.innerHTML = `
          <tr>
            <td colspan="8" class="text-center p-4 text-white bg-black bg-opacity-50 rounded">
              Nenhum produto encontrado ou cadastrado.
            </td>
          </tr>
        `;
        return;
      }

      
      dados.content.sort((a, b) => {
        const ordemStatus = { disponivel: 1, vendido: 2, utilizado: 3 };
        const statusA = a.status?.toLowerCase() || "disponivel";
        const statusB = b.status?.toLowerCase() || "disponivel";
        return (ordemStatus[statusA] || 4) - (ordemStatus[statusB] || 4);
      });

      dados.content.forEach((produto) => {
        const precoUnitarioNum = Number(produto.precoUnitario);
        const precoTotal = (produto.quantidade * precoUnitarioNum).toFixed(2);

        let unidadeId;
        let observacao;

        if (produto.status?.toLowerCase() === "disponivel") {
          unidadeId = produto.id;
          observacao = produto.observacao || "—";
        } else {
          unidadeId = produto.unidadeProduto?.id;
          observacao = produto.unidadeProduto?.observacao || "—";
        }

        const row = document.createElement("tr");
        row.setAttribute("data-id", unidadeId);

        row.innerHTML = `
          <td class="px-5 py-3 border-b border-gray-700 text-sm text-white">${
            produto.nome
          }</td>
          <td class="px-5 py-3 border-b border-gray-700 text-sm text-white">${
            produto.quantidade
          }</td>
          <td class="px-5 py-3 border-b border-gray-700 text-sm text-white">R$ ${precoUnitarioNum.toFixed(
            2
          )}</td>
          <td class="px-5 py-3 border-b border-gray-700 text-sm text-white">R$ ${precoTotal}</td>
          <td class="px-5 py-3 border-b border-gray-700 text-sm text-white">${
            produto.categoria ?? "Não disponível"
          }</td>
          <td class="px-6 py-4 border-b border-gray-700 text-sm text-white">${observacao}</td>
          <td class="px-5 py-3 border-b border-gray-700 text-sm">
            <span class="
              px-3 py-1 rounded-full text-xs font-semibold
              ${
                produto.status?.toLowerCase() === "disponivel"
                  ? "bg-green-600 text-white"
                  : ""
              }
              ${
                produto.status?.toLowerCase() === "vendido"
                  ? "bg-red-600 text-white"
                  : ""
              }
              ${
                produto.status?.toLowerCase() === "utilizado"
                  ? "bg-yellow-500 text-black"
                  : ""
              }
            ">
              ${
                produto.status
                  ? produto.status.charAt(0).toUpperCase() +
                    produto.status.slice(1).toLowerCase()
                  : "—"
              }
            </span>
          </td>
          <td class="px-5 py-3 border-b border-gray-700 text-sm text-white">
            <div class="flex items-center space-x-2 justify-center h-full">
              ${
                produto.status?.toLowerCase() === "disponivel"
                  ? `<button onclick="editarProduto(${produto.id})" class="bg-yellow-500 hover:bg-yellow-600 text-black font-semibold py-1 px-3 rounded shadow"><i class="fas fa-edit"></i></button>
                    <button onclick="abrirModalVenda(${produto.id}, ${produto.quantidade}, ${produto.precoUnitario})" class="bg-green-600 hover:bg-green-700 text-white font-semibold py-1 px-3 rounded shadow"><i class="fas fa-dollar-sign"></i></button>
                    <button onclick="excluirProduto(${produto.id})" class="bg-red-600 hover:bg-red-700 text-white font-semibold py-1 px-3 rounded shadow"><i class="fas fa-trash"></i></button>`
                  : `<button onclick="abrirModalObservacao(${unidadeId}, '${observacao}')" class="bg-blue-600 hover:bg-blue-700 text-white font-semibold py-1 px-3 rounded shadow"><i class="fas fa-sticky-note"></i></button>
                    <button onclick="excluirUnidade(${unidadeId})" class="bg-red-600 hover:bg-red-700 text-white font-semibold py-1 px-3 rounded shadow"><i class="fas fa-trash"></i></button>`
              }
            </div>
          </td>
        `;

        tbody.appendChild(row);
      });

      atualizarPaginacao(dados.totalPages);
    } catch (error) {
      console.error("Erro:", error.message);
      await alertaErro(
        "Erro ao carregar produtos",
        "Não foi possível carregar os dados dos produtos."
      );
    }
  }

  statusFiltroSelect.addEventListener("change", () => {
    paginaAtual = 0;
    carregarProdutos(
      paginaAtual,
      filtroAtual,
      tipoFiltroAtual,
      statusFiltroSelect.value
    );
  });

  function atualizarPaginacao(totalPaginas) {
    const paginacao = document.getElementById("paginacao-estoque");
    paginacao.innerHTML = "";

    if (totalPaginas <= 1) {
      return;
    }

    for (let i = 0; i < totalPaginas; i++) {
      const botao = document.createElement("button");
      botao.textContent = i + 1;
      botao.disabled = i === paginaAtual;
      botao.className = `mx-1 px-3 py-1 rounded ${
        i === paginaAtual ? "bg-orange-600 text-white" : "bg-gray-500"
      }`;

      botao.addEventListener("click", () => {
        paginaAtual = i;
        carregarProdutos(
          paginaAtual,
          filtroAtual,
          tipoFiltroAtual,
          statusFiltroSelect.value
        );
      });

      paginacao.appendChild(botao);
    }
  }

  btnBusca.addEventListener("click", () => {
    filtroAtual = inputBusca.value.trim();
    tipoFiltroAtual = tipoFiltroSelect.value;
    const statusAtual = statusFiltroSelect.value;
    paginaAtual = 0;
    carregarProdutos(paginaAtual, filtroAtual, tipoFiltroAtual, statusAtual);
  });

  inputBusca.addEventListener("keydown", (event) => {
    if (event.key === "Enter") {
      btnBusca.click();
    }
  });

  let produtoVendaId = null;
  let precoUnitarioVenda = 0;
  let quantidadeDisponivelVenda = 0;

  function abrirModalVenda(id, quantidade, precoUnitario) {
    produtoVendaId = id;
    precoUnitarioVenda = precoUnitario;
    quantidadeDisponivelVenda = quantidade;

    document.getElementById("quantidade-venda").value = "";
    document.getElementById(
      "preco-venda-info"
    ).textContent = `Preço unitário: R$ ${precoUnitario.toFixed(
      2
    )} | Disponível: ${quantidade}`;
    document.getElementById("modal-venda").classList.remove("hidden");
  }

  function fecharModalVenda() {
    document.getElementById("modal-venda").classList.add("hidden");
  }

  async function confirmarVenda() {
    const quantidadeVendida = parseInt(
      document.getElementById("quantidade-venda").value
    );

    if (!quantidadeVendida || quantidadeVendida <= 0) {
      return alertaErro("Erro", "Quantidade inválida");
    }

    if (quantidadeVendida > quantidadeDisponivelVenda) {
      return alertaErro("Erro", "Quantidade maior que estoque disponível");
    }

    const total = (quantidadeVendida * precoUnitarioVenda).toFixed(2);
    console.log("JSON para venda:", JSON.stringify({ quantidadeVendida }));

    try {
      const response = await fetch(
        `http://localhost:8080/produtos/${produtoVendaId}/vender`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({ quantidadeVendida }),
        }
      );

      if (!response.ok) throw new Error("Erro ao vender produto");

      await alertaSucesso(`Venda realizada! Valor total: R$ ${total}`);
      fecharModalVenda();
      carregarProdutos(
        paginaAtual,
        filtroAtual,
        tipoFiltroAtual,
        statusFiltroSelect.value
      );
    } catch (error) {
      await alertaErro("Erro ao vender produto", error.message);
    }
  }
  window.abrirModalVenda = abrirModalVenda;
  window.fecharModalVenda = fecharModalVenda;
  window.confirmarVenda = confirmarVenda;

  let produtoObservacaoId = null;

  function abrirModalObservacao(id, observacaoAtual) {
    produtoObservacaoId = id;
    document.getElementById("inputObservacao").value = observacaoAtual || "";
    document.getElementById("modalObservacao").classList.remove("hidden");
  }

  function fecharModalObservacao() {
    document.getElementById("modalObservacao").classList.add("hidden");
  }

  async function salvarObservacao() {
    const novaObservacao = document.getElementById("inputObservacao").value;

    try {
      const response = await fetch(
        `http://localhost:8080/unidades/${produtoObservacaoId}/observacao`,
        {
          method: "PATCH",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ observacao: novaObservacao }),
        }
      );

      if (!response.ok) throw new Error("Erro ao atualizar observação");

      await alertaSucesso("Observação atualizada com sucesso!");
      fecharModalObservacao();

      carregarProdutos(
        paginaAtual,
        filtroAtual,
        tipoFiltroAtual,
        statusFiltroSelect.value
      );
    } catch (err) {
      await alertaErro("Erro ao atualizar observação", err.message);
    }
  }

  window.abrirModalObservacao = abrirModalObservacao;
  window.fecharModalObservacao = fecharModalObservacao;
  window.salvarObservacao = salvarObservacao;

  carregarProdutos(
    paginaAtual,
    filtroAtual,
    tipoFiltroAtual,
    statusFiltroSelect.value
  );
});
