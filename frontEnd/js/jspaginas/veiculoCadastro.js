document.addEventListener("DOMContentLoaded", () => {
  const cadastroForm = document.getElementById("cadastro-veiculo-form");
  const buscaClienteInput = document.getElementById("busca-cliente");
  const sugestoesClientes = document.getElementById("sugestoes-clientes");
  const clienteIdInput = document.getElementById("cliente-id");
  const cancelarBtn = document.getElementById("cancelar-btn");

  let todosClientes = [];
  let veiculoId = null;
  let nomeCliente = "";
  let cpfCliente = "";

  const urlParams = new URLSearchParams(window.location.search);
  veiculoId = urlParams.get("id");

  // Função para carregar todos os clientes do backend
  async function carregarClientesDoBackend() {
    try {
      const response = await fetch("http://localhost:8080/clientes");
      if (!response.ok) throw new Error("Erro ao buscar clientes");

      const dados = await response.json();
      if (Array.isArray(dados)) {
        todosClientes = dados;
      } else if (Array.isArray(dados.content)) {
        todosClientes = dados.content;
      } else {
        console.error("Resposta do backend não é um array:", dados);
        todosClientes = [];
      }
    } catch (erro) {
      console.error("Erro ao carregar clientes:", erro);
      todosClientes = [];
      await alertaErro("Erro ao carregar clientes", erro.message);
    }
  }

  // Função para carregar veículo para edição
  async function carregarVeiculoParaEdicao(id) {
    try {
      const response = await fetch(`http://localhost:8080/veiculos/${id}`);
      if (!response.ok) throw new Error("Erro ao buscar veículo");

      const veiculo = await response.json();
      document.getElementById("id").value = veiculo.id;
      document.getElementById("modelo").value = veiculo.modelo;
      document.getElementById("placa").value = veiculo.placa;
      document.getElementById("ano").value = veiculo.ano;
      document.getElementById("cor").value = veiculo.cor;

      if (veiculo.cliente) {
        buscaClienteInput.value = veiculo.cliente.nome;
        clienteIdInput.value = veiculo.cliente.id;
        nomeCliente = veiculo.cliente.nome;
        cpfCliente = veiculo.cliente.cpf;
      }
    } catch (erro) {
      console.error("Erro ao carregar veículo:", erro);
      await alertaErro(
        "Erro ao carregar veículo",
        "Não foi possível carregar os dados do veículo."
      );
    }
  }

  if (veiculoId) {
    carregarVeiculoParaEdicao(veiculoId);
  }

  // Mostrar sugestões de clientes
  function mostrarSugestoes(filtro) {
    const filtrados = todosClientes.filter((cliente) =>
      cliente.nome.toLowerCase().includes(filtro.toLowerCase())
    );

    sugestoesClientes.innerHTML = "";

    if (filtrados.length === 0) {
      sugestoesClientes.classList.add("hidden");
      return;
    }

    filtrados.forEach((cliente) => {
      const li = document.createElement("li");
      li.textContent = `${cliente.nome} - ${cliente.cpf}`;
      li.classList.add(
        "cursor-pointer",
        "p-2",
        "hover:bg-gray-700",
        "hover:text-white"
      );

      li.addEventListener("click", () => {
        buscaClienteInput.value = cliente.nome;
        clienteIdInput.value = cliente.id;
        nomeCliente = cliente.nome;
        cpfCliente = cliente.cpf;

        sugestoesClientes.classList.add("hidden");
      });

      sugestoesClientes.appendChild(li);
    });

    sugestoesClientes.classList.remove("hidden");
  }

  // Eventos do input de cliente
  buscaClienteInput.addEventListener("input", () => {
    const valor = buscaClienteInput.value.trim();
    if (valor.length > 0) {
      mostrarSugestoes(valor);
    } else {
      sugestoesClientes.classList.add("hidden");
      clienteIdInput.value = "";
    }
  });

  buscaClienteInput.addEventListener("focus", () => {
    mostrarSugestoes(buscaClienteInput.value.trim());
  });

  // Esconder sugestões ao clicar fora
  document.addEventListener("click", (e) => {
    if (
      !buscaClienteInput.contains(e.target) &&
      !sugestoesClientes.contains(e.target)
    ) {
      sugestoesClientes.classList.add("hidden");
    }
  });

  document.getElementById("placa").addEventListener("input", (e) => {
    e.target.value = e.target.value.toUpperCase();
  });
  // Validação simples
  function validarFormulario() {
    let valido = true;

    if (!clienteIdInput.value) {
      document.getElementById("cliente-error").textContent =
        "Selecione um cliente da lista.";
      valido = false;
    } else {
      document.getElementById("cliente-error").textContent = "";
    }

    const placaInput = document.getElementById("placa").value.trim();

    if (!placaInput) {
      document.getElementById("placa-error").textContent =
        "Preencha a placa do veículo.";
      valido = false;
    } else if (placaInput.length !== 7) {
      document.getElementById("placa-error").textContent =
        "A placa deve ter exatamente 7 caracteres.";
      valido = false;
    } else {
      document.getElementById("placa-error").textContent = "";
    }

    if (!document.getElementById("modelo").value.trim()) {
      document.getElementById("modelo-error").textContent =
        "Preencha o modelo do veículo.";
      valido = false;
    } else {
      document.getElementById("modelo-error").textContent = "";
    }

    return valido;
  }

  // Submissão do formulário com alertas estilizados
  cadastroForm.addEventListener("submit", async (e) => {
    e.preventDefault();

    if (!validarFormulario()) return;

    const id = document.getElementById("id").value;
    const modelo = document.getElementById("modelo").value.trim();
    const placa = document.getElementById("placa").value.trim();
    const ano = document.getElementById("ano").value.trim();
    const cor = document.getElementById("cor").value.trim();
    const idCliente = clienteIdInput.value;

    const veiculo = {
      modelo,
      placa,
      ano,
      cor,
      cliente: {
        id: idCliente,
      },
    };

    console.log(
      "Dados do veículo a serem enviados:",
      JSON.stringify(veiculo, null, 2)
    );

    try {
      let response;
      if (id) {
        response = await fetch(`http://localhost:8080/veiculos/${id}`, {
          method: "PUT",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(veiculo),
        });
      } else {
        response = await fetch("http://localhost:8080/veiculos", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(veiculo),
        });
      }

      if (!response.ok) {
        const erroTexto = await response.text();
        throw new Error(erroTexto || "Erro ao salvar veículo");
      }

      await alertaSucesso(
        id ? "Veículo atualizado!" : "Veículo cadastrado!",
        `O veículo ${modelo} foi ${
          id ? "atualizado" : "cadastrado"
        } com sucesso.`
      );

      window.location.href = "veiculo.html";
    } catch (erro) {
      console.error("Erro ao salvar veículo:", erro.message);
      await alertaErro("Erro ao salvar veículo", erro.message);
    }
  });

  cancelarBtn.addEventListener("click", () => {
    window.location.href = "veiculo.html";
  });

  // Inicializa carregamento de clientes
  carregarClientesDoBackend();
});
