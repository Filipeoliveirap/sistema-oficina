let paginaAtual = 0;
const tamanhoPagina = 10;

// Funções globais para editar e excluir veículos
function editarVeiculo(id) {
  window.location.href = `veiculosCadastro.html?id=${id}`;
}

async function excluirVeiculo(id) {
  const confirmado = await confirmarAcao(
    "Tem certeza que deseja excluir este veículo?"
  );
  if (!confirmado) return;

  try {
    const response = await fetch(`http://localhost:8080/veiculos/${id}`, {
      method: "DELETE",
    });

    if (response.status === 204) {
      const linha = document.querySelector(`tr[data-id="${id}"]`);
      if (linha) linha.remove();
      await alertaSucesso("Veículo excluído com sucesso!");
    } else {
      throw new Error("Erro ao excluir o veículo");
    }
  } catch (error) {
    console.error("Erro:", error.message);
    await alertaErro("Erro ao excluir veículo", error.message);
  }
}

document.addEventListener("DOMContentLoaded", function () {
  const tbody = document.querySelector("#tabela-veiculos tbody");
  const inputBusca = document.getElementById("pesquisar-veiculo");
  const btnBusca = document.getElementById("btn-buscar-veiculo");
  const tipoFiltroSelect = document.getElementById("tipo-filtro");

  let filtroAtual = "";
  let tipoFiltroAtual = "modelo";

  // Função para carregar veículos da API com paginação e filtro
  async function carregarVeiculos(pagina, filtro = "", tipoFiltro = "modelo") {
    try {
      let url = `http://localhost:8080/veiculos?pagina=${pagina}&tamanho=${tamanhoPagina}&tipoFiltro=${encodeURIComponent(
        tipoFiltro
      )}`;
      if (filtro) {
        url += `&filtro=${encodeURIComponent(filtro)}`;
      }

      const response = await fetch(url);
      if (!response.ok) throw new Error("Erro ao buscar veículos");

      const dados = await response.json();
      tbody.innerHTML = "";

      if (!dados.content || dados.content.length === 0) {
        tbody.innerHTML = `
                    <tr>
                        <td colspan="7" class="text-center p-4 text-white bg-black bg-opacity-50 rounded">
                            Nenhum veículo encontrado ou cadastrado.
                        </td>
                    </tr>
                `;
        atualizarPaginacao(0);
        return;
      }

      dados.content.forEach((veiculo) => {
        const row = document.createElement("tr");
        row.setAttribute("data-id", veiculo.id);
        row.innerHTML = `
                    <td class="px-5 py-5 border-b border-gray-700 text-white">${
                      veiculo.modelo
                    }</td>
                    <td class="px-5 py-5 border-b border-gray-700 text-white">${
                      veiculo.placa
                    }</td>
                    <td class="px-5 py-5 border-b border-gray-700 text-white">${
                      veiculo.ano
                    }</td>
                    <td class="px-5 py-5 border-b border-gray-700 text-white">${
                      veiculo.cor
                    }</td>
                    <td class="px-5 py-5 border-b border-gray-700 text-white">${
                      veiculo.cliente?.nome || ""
                    }</td>
                    <td class="px-5 py-5 border-b border-gray-700 text-white">${
                      veiculo.cliente?.cpf || ""
                    }</td>
                    <td class="px-5 py-5 border-b border-gray-700 text-center">
                        <button onclick="editarVeiculo(${
                          veiculo.id
                        })" class="bg-yellow-500 hover:bg-yellow-600 text-black font-semibold py-1 px-3 rounded shadow">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button onclick="excluirVeiculo(${
                          veiculo.id
                        })" class="bg-red-600 hover:bg-red-700 text-white font-semibold py-1 px-3 rounded shadow">
                            <i class="fas fa-trash"></i>
                        </button>
                    </td>
                `;
        tbody.appendChild(row);
      });

      atualizarPaginacao(dados.totalPages);
    } catch (error) {
      console.error("Erro:", error.message);
      tbody.innerHTML = `
                <tr>
                    <td colspan="7" class="text-center p-4 text-red-600">
                        Erro ao carregar veículos: ${error.message}
                    </td>
                </tr>
            `;
      await alertaErro("Erro ao carregar veículos", error.message);
    }
  }

  function atualizarPaginacao(totalPaginas) {
    const paginacao = document.getElementById("paginacao");
    paginacao.innerHTML = "";

    if (totalPaginas <= 1) return;

    for (let i = 0; i < totalPaginas; i++) {
      const botao = document.createElement("button");
      botao.textContent = i + 1;
      botao.disabled = i === paginaAtual;
      botao.className = `mx-1 px-3 py-1 rounded ${
        i === paginaAtual ? "bg-orange-600 text-white" : "bg-gray-500"
      }`;
      botao.addEventListener("click", () => {
        paginaAtual = i;
        carregarVeiculos(paginaAtual, filtroAtual, tipoFiltroAtual);
      });
      paginacao.appendChild(botao);
    }
  }

  btnBusca.addEventListener("click", () => {
    filtroAtual = inputBusca.value.trim();
    tipoFiltroAtual = tipoFiltroSelect.value;
    paginaAtual = 0;
    carregarVeiculos(paginaAtual, filtroAtual, tipoFiltroAtual);
  });

  inputBusca.addEventListener("keypress", (e) => {
    if (e.key === "Enter") {
      e.preventDefault();
      btnBusca.click();
    }
  });

  // Carregar primeira página
  carregarVeiculos(paginaAtual);
});
