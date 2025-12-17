document.addEventListener("DOMContentLoaded", () => {
  const form = document.getElementById("cadastro-servico-form");
  const buscaClienteInput = document.getElementById("busca-cliente");
  const sugestoesClientes = document.getElementById("sugestoes-clientes");
  const clienteIdInput = document.getElementById("cliente-id");
  const cancelarBtn = document.getElementById("cancelar-btn");
  const buscaVeiculoInput = document.getElementById("busca-veiculo");
  const sugestoesVeiculos = document.getElementById("sugestoes-veiculos");
  const veiculoIdInput = document.getElementById("veiculo-id");
  const produtosContainer = document.getElementById("produtos-container");
  const adicionarProdutoBtn = document.getElementById("adicionar-produto-btn");

  let clienteSelecionado = null;
  let veiculoSelecionado = null;

  let todosClientes = [];
  let servicoId = null;
  let cpfCliente = "";
  let veiculosDoCliente = [];

  const urlParams = new URLSearchParams(window.location.search);
  servicoId = urlParams.get("id");

  //CLIENTES E VEÍCULOS 

  async function carregarClientesDoBackend() {
    try {
      const response = await fetch("http://localhost:8080/clientes");
      if (!response.ok) throw new Error("Erro ao buscar clientes");
      const dados = await response.json();
      todosClientes = Array.isArray(dados.content) ? dados.content : [];
    } catch (erro) {
      console.error("Erro ao carregar clientes:", erro);
      todosClientes = [];
    }
  }

  async function carregarVeiculosDoCliente(clienteId) {
    try {
      const response = await fetch(
        `http://localhost:8080/veiculos/buscar/cpf?cpf=${cpfCliente}`
      );
      if (!response.ok) throw new Error("Erro ao buscar veículos do cliente");
      veiculosDoCliente = await response.json();
    } catch (erro) {
      console.error("Erro ao carregar veículos:", erro);
      veiculosDoCliente = [];
    }
  }

  // AUTOCOMPLETE CLIENTES 

  function mostrarSugestoes(filtro) {
    const filtrados = todosClientes.filter((cliente) =>
      cliente.nome.toLowerCase().includes(filtro.toLowerCase())
    );

    sugestoesClientes.innerHTML = "";
    filtrados.forEach((cliente) => {
      const li = document.createElement("li");
      li.textContent = `${cliente.nome} - ${cliente.cpf}`;
      li.classList.add(
        "cursor-pointer",
        "p-2",
        "hover:bg-gray-700",
        "hover:text-white"
      );
      li.addEventListener("click", async () => {
        buscaClienteInput.value = `${cliente.nome} - ${cliente.cpf}`;
        clienteSelecionado = cliente; 
        clienteIdInput.value = cliente.id;
        cpfCliente = cliente.cpf;
        sugestoesClientes.classList.add("hidden");
        await carregarVeiculosDoCliente(cliente.id);
      });

      sugestoesClientes.appendChild(li);
    });
    if (filtrados.length > 0) sugestoesClientes.classList.remove("hidden");
  }

  buscaClienteInput.addEventListener("input", () => {
    const valor = buscaClienteInput.value.trim();
    if (valor.length > 0) mostrarSugestoes(valor);
  });

  buscaClienteInput.addEventListener("focus", () =>
    mostrarSugestoes(buscaClienteInput.value.trim())
  );

  //  AUTOCOMPLETE VEÍCULOS 

  buscaVeiculoInput.addEventListener("input", () => {
    const termo = buscaVeiculoInput.value.trim();
    if (termo.length < 2 || veiculosDoCliente.length === 0) {
      sugestoesVeiculos.innerHTML = "";
      sugestoesVeiculos.classList.add("hidden");
      return;
    }

    const filtrados = veiculosDoCliente.filter(
      (v) =>
        v.modelo.toLowerCase().includes(termo.toLowerCase()) ||
        v.placa.toLowerCase().includes(termo.toLowerCase())
    );

    sugestoesVeiculos.innerHTML = "";
    filtrados.forEach((veiculo) => {
      const li = document.createElement("li");
      li.textContent = `${veiculo.modelo} - ${veiculo.placa}`;
      li.classList.add(
        "cursor-pointer",
        "p-2",
        "hover:bg-gray-700",
        "hover:text-white"
      );
      li.addEventListener("click", () => {
        buscaVeiculoInput.value = `${veiculo.modelo} - ${veiculo.placa}`;
        veiculoSelecionado = veiculo;
        veiculoIdInput.value = veiculo.id;
        sugestoesVeiculos.classList.add("hidden");
      });
      sugestoesVeiculos.appendChild(li);
    });
    sugestoesVeiculos.classList.remove("hidden");
  });

  //PRODUTOS USADOS 

  async function buscarProdutos(termo) {
    if (!termo || termo.length < 2) return [];
    try {
      const response = await fetch(
        `http://localhost:8080/produtos?nome=${encodeURIComponent(termo)}`
      );
      if (!response.ok) throw new Error("Erro ao buscar produtos");
      return await response.json();
    } catch (erro) {
      console.error("Erro ao buscar produtos:", erro);
      return [];
    }
  }

  function adicionarProdutoLinha(prod = { id: null, nome: "", quantidade: 1 }) {
    const div = document.createElement("div");
    div.classList.add("flex", "space-x-2", "items-center", "relative");

    div.innerHTML = `
      <input type="text" class="flex-1 pl-2 py-1 rounded-md bg-[#2D2D2D] text-white border border-gray-600" placeholder="Nome do produto" value="${
        prod.nome
      }" required>
      <input type="hidden" class="produto-id" value="${prod.id || ""}">
      <ul class="absolute z-10 w-full bg-[#1E1E1E] border border-gray-600 rounded-md mt-1 hidden max-h-40 overflow-y-auto text-white"></ul>
      <input type="number" min="1" class="w-20 pl-2 py-1 rounded-md bg-[#2D2D2D] text-white border border-gray-600" value="${
        prod.quantidade
      }" required>
      <button type="button" class="bg-red-600 hover:bg-red-700 px-2 py-1 rounded-md text-white"><i class="fas fa-trash"></i></button>
    `;

    const inputProduto = div.querySelector("input[type=text]");
    const inputProdutoId = div.querySelector(".produto-id");
    const sugestoesProduto = div.querySelector("ul");
    const btnRemover = div.querySelector("button");

    btnRemover.addEventListener("click", () => div.remove());

    inputProduto.addEventListener("input", async () => {
      const termo = inputProduto.value.trim();
      if (!termo || termo.length < 2) {
        sugestoesProduto.innerHTML = "";
        sugestoesProduto.classList.add("hidden");
        return;
      }

      const produtosFiltradosRaw = await buscarProdutos(termo);
      const produtosFiltrados = Array.isArray(produtosFiltradosRaw)
        ? produtosFiltradosRaw
        : produtosFiltradosRaw.content || produtosFiltradosRaw.data || [];

      sugestoesProduto.innerHTML = "";

      produtosFiltrados.forEach((p) => {
        const li = document.createElement("li");
        li.textContent = p.nome;
        li.classList.add(
          "cursor-pointer",
          "p-2",
          "hover:bg-gray-700",
          "hover:text-white"
        );
        li.addEventListener("click", () => {
          inputProduto.value = p.nome;
          inputProdutoId.value = p.id;
          sugestoesProduto.classList.add("hidden");
        });
        sugestoesProduto.appendChild(li);
      });

      sugestoesProduto.classList.toggle(
        "hidden",
        produtosFiltrados.length === 0
      );
    });

    document.addEventListener("click", (e) => {
      if (!div.contains(e.target)) {
        sugestoesProduto.classList.add("hidden");
      }
    });

    produtosContainer.appendChild(div);
  }

  adicionarProdutoBtn.addEventListener("click", () => adicionarProdutoLinha());

  // CARREGAR SERVIÇO PARA EDIÇÃO 

  async function carregarServicoParaEdicao(id) {
    try {
      const response = await fetch(`http://localhost:8080/servicos/${id}`);
      if (!response.ok) throw new Error(await response.text());
      const servico = await response.json();

      document.getElementById("descricao").value = servico.descricao;
      document.getElementById("preco").value = servico.preco;
      document.getElementById("data").value = servico.data.split("T")[0];

      buscaClienteInput.value = `${servico.cliente.nome} - ${servico.cliente.cpf}`;
      clienteSelecionado = servico.cliente; 
      clienteIdInput.value = servico.cliente.id;
      cpfCliente = servico.cliente.cpf;

      await carregarVeiculosDoCliente(servico.cliente.id);

      if (
        servico.veiculo &&
        !veiculosDoCliente.find((v) => v.id === servico.veiculo.id)
      ) {
        veiculosDoCliente.push(servico.veiculo);
      }

      if (servico.veiculo) {
        buscaVeiculoInput.value = `${servico.veiculo.modelo} - ${servico.veiculo.placa}`;
        veiculoSelecionado = servico.veiculo; 
        veiculoIdInput.value = servico.veiculo.id;
      }

      if (Array.isArray(servico.produtosUsados)) {
        servico.produtosUsados.forEach((prod) =>
          adicionarProdutoLinha({
            id: prod.id, 
            nome: prod.nome || "", 
            quantidade: prod.quantidade,
          })
        );
      }

      console.log("Produtos recebidos para edição:", servico.produtosUsados);
    } catch (error) {
      alertaErro("Erro ao carregar serviço. Tente novamente.");
      alertaErro(
        "Erro ao carregar serviço. Verifique os dados e tente novamente."
      );
    }
  }

  if (servicoId) carregarServicoParaEdicao(servicoId);

  // -------------------- VALIDAR FORMULÁRIO --------------------

  function validarFormulario() {
    let valido = true;
    if (!clienteSelecionado) {
      document.getElementById("cliente-error").textContent =
        "Selecione um cliente da lista.";
      valido = false;
    } else document.getElementById("cliente-error").textContent = "";

    if (!veiculoSelecionado) {
      document.getElementById("veiculo-error").textContent =
        "Selecione um veículo.";
      valido = false;
    } else document.getElementById("veiculo-error").textContent = "";

    return valido;
  }

  // -------------------- ENVIO FORMULÁRIO --------------------

  form.addEventListener("submit", async (e) => {
    e.preventDefault();
    if (!validarFormulario()) return;

    const descricao = document.getElementById("descricao").value.trim();
    const preco = parseFloat(document.getElementById("preco").value.trim());
    const data = document.getElementById("data").value;

    const produtosUsados = Array.from(produtosContainer.children).map((div) => {
      const produtoId = div.querySelector(".produto-id").value;
      const quantidade = parseInt(
        div.querySelector("input[type=number]").value
      );
      return { produtoId: parseInt(produtoId), quantidade };
    });

    const dadosServico = {
      descricao,
      preco,
      data: `${data}T00:00:00`,
      cliente: clienteSelecionado
        ? {
            id: clienteSelecionado.id,
            nome: clienteSelecionado.nome,
            cpf: clienteSelecionado.cpf,
          }
        : null,
      veiculo: veiculoSelecionado
        ? {
            id: veiculoSelecionado.id,
            modelo: veiculoSelecionado.modelo,
            placa: veiculoSelecionado.placa,
          }
        : null,
      produtosUsados: produtosUsados.map((p) => ({
        id: p.produtoId, // backend espera "id"
        quantidade: p.quantidade,
      })),
    };

    console.log("📝 Dados do serviço a serem enviados (objeto):", dadosServico);
    console.log(
      "📝 Dados do serviço a serem enviados (JSON):",
      JSON.stringify(dadosServico, null, 2)
    );

    try {
      const url = servicoId
        ? `http://localhost:8080/servicos/${servicoId}`
        : "http://localhost:8080/servicos";
      const method = servicoId ? "PUT" : "POST";

      const response = await fetch(url, {
        method,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(dadosServico),
      });

      if (!response.ok) throw new Error(await response.text());

      await alertaSucesso("Sucesso!", "Serviço salvo com sucesso!");
      window.location.href = "servicos.html";
    } catch (error) {
      console.error("Erro ao salvar serviço:", error);
      await alertaErro("Erro ao salvar serviço", error.message);
    }
  });

  cancelarBtn.addEventListener(
    "click",
    () => (window.location.href = "servicos.html")
  );

  carregarClientesDoBackend();
});
