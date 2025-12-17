document.addEventListener("DOMContentLoaded", () => {
  const tabelaServicos = document.querySelector("#tabela-servicos tbody");
  const filtroInput = document.getElementById("pesquisar-servico");
  const tipoFiltroSelect = document.getElementById("tipo-filtro");
  const btnBuscar = document.getElementById("btn-buscar-servico");

  const API_BASE_URL = "http://localhost:8080";

  const modal = document.getElementById("modalFinalizarServico");
  const inputValor = document.getElementById("inputValorGarantia");
  const selectUnidade = document.getElementById("inputUnidadeGarantia");
  const btnSalvar = document.getElementById("btnSalvarFinalizacao");
  const btnCancelar = document.getElementById("btnCancelarFinalizacao");

  let servicoIdAtual = null; // guarda o id do serviço que está sendo finalizado

  function corrigirFusoHorario(dataISO) {
    const data = new Date(dataISO);
    data.setMinutes(data.getMinutes() + data.getTimezoneOffset());
    return data;
  }

  function formatarData(dataISO) {
    const dataCorrigida = corrigirFusoHorario(dataISO);
    const dia = String(dataCorrigida.getDate()).padStart(2, "0");
    const mes = String(dataCorrigida.getMonth() + 1).padStart(2, "0");
    const ano = dataCorrigida.getFullYear();
    return `${dia}/${mes}/${ano}`;
  }

  async function carregarServicos(servicos = null) {
    if (!servicos) {
      servicos = await buscarServicosBackend();
    }

    tabelaServicos.innerHTML = "";

    if (servicos.length === 0) {
      tabelaServicos.innerHTML =
        '<tr><td colspan="10" class="text-center p-4 text-white bg-black bg-opacity-50 rounded">Nenhum serviço encontrado ou cadastrado.</td></tr>';
      return;
    }

    servicos.forEach((servico) => {
      const produtosHtml =
        servico.produtosUsados && servico.produtosUsados.length > 0
          ? servico.produtosUsados
              .map(
                (p) => `
            <span class="inline-block bg-gray-800 text-white text-xs font-semibold px-2 py-1 rounded-full mr-1 mb-1">
              ${p.nome || p.produto?.nome} (x${p.quantidade})
            </span>
          `
              )
              .join("")
          : '<span class="text-gray-400">Nenhum</span>';

      const tr = document.createElement("tr");
      tr.id = `servico-${servico.id}`;
      tr.innerHTML = `
      <td class="px-5 py-3 border-b border-gray-700 text-white">${
        servico.descricao
      }</td>
      <td class="px-5 py-3 border-b border-gray-700 text-white">R$ ${
        servico.preco
      }</td>
      <td class="px-5 py-3 border-b border-gray-700 text-white">${formatarData(
        servico.data
      )}</td>
      <td class="px-5 py-3 border-b border-gray-700 text-white">${
        servico.cliente?.nome || ""
      }</td>
      <td class="px-5 py-3 border-b border-gray-700 text-white">${
        servico.cliente?.cpf || ""
      }</td>
      <td class="px-5 py-3 border-b border-gray-700 text-white">${
        servico.veiculo
          ? `${servico.veiculo.modelo} - ${servico.veiculo.placa}`
          : ""
      }</td>
      <td class="px-5 py-3 border-b border-gray-700 text-white">${produtosHtml}</td>
      <td class="px-5 py-3 border-b border-gray-700 ">
        <div class="flex gap-2">
          <button onclick="editarServico(${
            servico.id
          })" class="bg-yellow-500 hover:bg-yellow-600 text-black font-semibold py-1 px-3 rounded shadow"><i class="fas fa-edit"></i></button>
          <button onclick="excluirServico(${
            servico.id
          })" class="bg-red-600 hover:bg-red-700 text-white font-semibold py-1 px-3 rounded shadow"><i class="fas fa-trash"></i></button>
          <button onclick="finalizarServico(${
            servico.id
          })" class="bg-orange-500 hover:bg-orange-600 text-white font-semibold py-1 px-3 rounded shadow "><i class="fas fa-check"></i></button>
        </div>
      </td>
    `;
      tabelaServicos.appendChild(tr);
    });
  }

  async function buscarServicos() {
    const termo = filtroInput.value.trim();
    try {
      const response = await fetch(
        `${API_BASE_URL}/servicos/buscar-tudo?termo=${encodeURIComponent(
          termo
        )}`
      );
      if (!response.ok) throw new Error("Erro ao buscar serviços");
      const servicos = await response.json();
      carregarServicos(servicos);
    } catch (error) {
      console.error("Erro:", error);
      alert("Erro ao buscar serviços");
    }
  }

  async function buscarServicosBackend() {
    try {
      const response = await fetch(`${API_BASE_URL}/servicos`);
      if (!response.ok) throw new Error("Erro ao carregar serviços");
      const servicos = await response.json();
      return servicos;
    } catch (error) {
      console.error("Erro:", error);
      return [];
    }
  }

  window.editarServico = (id) => {
    window.location.href = `servicosCadastro.html?id=${id}`;
  };

  window.excluirServico = async (id) => {
    const confirmado = await confirmarAcao(
      "Tem certeza que deseja excluir este serviço?"
    );
    if (!confirmado) return;

    try {
      const response = await fetch(`${API_BASE_URL}/servicos/${id}`, {
        method: "DELETE",
      });
      if (!response.ok) throw new Error("Erro ao excluir serviço");

      alertaSucesso("Excluído!", "Serviço excluído com sucesso!");
      carregarServicos();
    } catch (error) {
      console.error("Erro:", error);
      alert("Erro ao excluir serviço");
    }
  };

  // Função para abrir modal
  window.finalizarServico = (id) => {
    servicoIdAtual = id;
    inputValor.value = "";
    selectUnidade.value = "mes";
    modal.classList.remove("hidden");
  };

  // Cancelar modal
  btnCancelar.onclick = () => {
    modal.classList.add("hidden");
  };

  // Salvar modal - definido **uma vez só**
  btnSalvar.onclick = async () => {
    const valor = parseInt(inputValor.value);
    const unidade = selectUnidade.value;
    const detalhesFinalizacao = document
      .getElementById("inputDetalhesFinalizacao")
      .value.trim(); // pegando o valor do textarea

    if (!valor || valor < 1 || valor > 10) {
      alert("Digite um número entre 1 e 10 para a garantia!");
      return;
    }

    const dataAtual = new Date();
    let dataGarantia = new Date(dataAtual);

    if (unidade === "mes") {
      dataGarantia.setMonth(dataGarantia.getMonth() + valor);
    } else if (unidade === "ano") {
      dataGarantia.setFullYear(dataGarantia.getFullYear() + valor);
    }

    const clausula =
      valor > 1
        ? `${valor} ${unidade === "mes" ? "meses" : "anos"}`
        : `${valor} ${unidade === "mes" ? "mês" : "ano"}`;

    const detalhes = inputDetalhesFinalizacao.value.trim() || null; // pega do input do modal

    const payload = {
      servicoId: servicoIdAtual,
      clausulaGarantia: clausula,
      dataGarantia: dataGarantia.toISOString().split("T")[0],
      detalhesFinalizacao: detalhes,
    };

    console.log(
      "JSON completo enviado para finalizar serviço:",
      JSON.stringify(payload, null, 2)
    );

    try {
      const response = await fetch(
        `${API_BASE_URL}/servicos-finalizados/${servicoIdAtual}/finalizar`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(payload),
        }
      );

      if (!response.ok) {
        const msgErro = await response.text();
        throw new Error(msgErro || "Erro ao finalizar serviço.");
      }

      alertaSucesso("Finalizado!", "Serviço finalizado com sucesso!");
      modal.classList.add("hidden");

      const linha = document.getElementById(`servico-${servicoIdAtual}`);
      if (linha) linha.remove();
    } catch (error) {
      console.error("Erro ao finalizar serviço:", error);
      alert(`Erro ao finalizar serviço: ${error.message}`);
    }
  };

  btnBuscar.addEventListener("click", buscarServicos);
  carregarServicos();
});
