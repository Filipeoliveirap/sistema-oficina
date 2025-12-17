document.addEventListener('DOMContentLoaded', () => {
  // --- INÍCIO SIMULAÇÃO DE DADOS (apagar essa parte depois) ---

  const data = {
    totalClientes: 150,
    servicosAndamento: 30,
    totalEstoque: 75,
    servicosFinalizados: 120,
    ultimosServicos: [
      {
        nomeCliente: "João Silva",
        descricao: "Troca de óleo",
        dataInicio: "01/05/2025",
        data: "03/05/2025",
        valor: 150.00,
        observacoes: "Serviço rápido e eficiente"
      },
      {
        nomeCliente: "Maria Oliveira",
        descricao: "Revisão geral",
        dataInicio: "28/04/2025",
        data: "30/04/2025",
        valor: 450.50,
        observacoes: ""
      },
      {
        nomeCliente: "Carlos Souza",
        descricao: "Alinhamento e balanceamento",
        dataInicio: "27/04/2025",
        data: "29/04/2025",
        valor: 200.00,
        observacoes: "Cliente pediu para revisar pneus"
      },
      {
        nomeCliente: "Ana Pereira",
        descricao: "Substituição de freios",
        dataInicio: "25/04/2025",
        data: "27/04/2025",
        valor: 320.75,
        observacoes: ""
      },
      {
        nomeCliente: "Roberto Lima",
        descricao: "Troca de bateria",
        dataInicio: "24/04/2025",
        data: "25/04/2025",
        valor: 180.00,
        observacoes: "Peça original"
      }
    ],
    servicosPorMes: {
      "Fev/2025": 15,
      "Mar/2025": 22,
      "Abr/2025": 18,
      "Mai/2025": 30
    }
  };

  // Chama a função que preenche a página com os dados simulados
  preencherDashboard(data);
  // --- FIM SIMULAÇÃO DE DADOS ---

  // --- APAGAR O FETCH ABAIXO QUANDO USAR SIMULAÇÃO ---


  /*fetch('http://localhost:8080/api/dashboard')
    .then(response => {
      if (!response.ok) throw new Error('Erro na resposta da API');
      return response.json();
    })
    .then(data => {
      preencherDashboard(data);
    })
    .catch(error => {
      console.error('Erro ao carregar dashboard:', error);
    });
    */

  // Função para preencher dashboard (usada tanto com dados reais quanto simulados)
  function preencherDashboard(data) {
    console.log('Dados do dashboard:', data); // debug

    // Atualiza os cards com contagens
    document.getElementById('totalClientes').textContent = data.totalClientes;
    document.getElementById('servicosAndamento').textContent = data.servicosAndamento;
    document.getElementById('totalEstoque').textContent = data.totalEstoque;
    document.getElementById('servicosFinalizados').textContent = data.servicosFinalizados;

    // Preenche a tabela dos últimos 5 serviços finalizados
    const tbody = document.querySelector('#historicoServicos tbody');
    tbody.innerHTML = ''; // limpa a tabela antes

    data.ultimosServicos.forEach(servico => {
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td class="px-6 py-5 align-top rounded-l-lg">${servico.nomeCliente || '-'}</td>
        <td class="px-6 py-5 align-top">${servico.descricao || '-'}</td>
        <td class="px-6 py-5 align-top">${servico.dataInicio || '-'}</td>
        <td class="px-6 py-5 align-top">${servico.data || '-'}</td>
        <td class="px-6 py-5 align-top">R$ ${servico.valor ? servico.valor.toFixed(2).replace('.', ',') : '-'}</td>
        <td class="px-6 py-5 align-top rounded-r-lg">${servico.observacoes || '-'}</td>
      `;
      tbody.appendChild(tr);
    });

    // Monta gráfico de barras com serviços por mês (últimos 4)
    const ctx = document.getElementById('graficoServicos').getContext('2d');
    const labels = Object.keys(data.servicosPorMes); // Ex: ["Fev/2025", "Mar/2025", "Abr/2025", "Mai/2025"]
    const values = Object.values(data.servicosPorMes);

    // Destrói gráfico anterior, se houver (para evitar sobreposição ao atualizar)
    if (window.graficoDashboard) {
      window.graficoDashboard.destroy();
    }

    window.graficoDashboard = new Chart(ctx, {
      type: 'bar',
      data: {
        labels: labels,
        datasets: [{
          label: 'Serviços por mês',
          data: values,
          backgroundColor: 'rgba(54, 162, 235, 0.6)',
          borderRadius: 5
        }]
      },
      options: {
        responsive: true,
        plugins: {
          legend: {
            display: false
          },
          tooltip: {
            callbacks: {
              label: ctx => `${ctx.parsed.y} serviço(s)`
            }
          }
        },
        scales: {
          y: {
            beginAtZero: true,
            ticks: {
              precision: 0
            },
            title: {
              display: true,
              text: 'Quantidade'
            }
          },
          x: {
            title: {
              display: true,
              text: 'Mês/Ano'
            }
          }
        }
      }
    });
  }
});
