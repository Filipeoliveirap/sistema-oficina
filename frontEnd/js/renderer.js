// Ação do botão "Fechar"
document.getElementById('btnFechar').addEventListener('click', async () => {
  const resultado = await Swal.fire({
    title: 'Tem certeza?',
    text: 'Deseja realmente fechar o aplicativo?',
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#3085d6',
    cancelButtonColor: '#d33',
    confirmButtonText: 'Sim, fechar',
    cancelButtonText: 'Cancelar'
  });

  if (resultado.isConfirmed) {
    window.electronAPI.sairDoApp();
  }
});

// Troca de páginas no iframe
function carregarPagina(pagina) {
  const iframe = document.getElementById('conteudo-frame');
  switch (pagina) {
    case 'dashboard':
      iframe.src = '../html/dashboard.html';
      break;
    case 'clientes':
      iframe.src = '../html/clientes.html';
      break;
    case 'servicos':
      iframe.src = '../html/servicos.html';
      break;
    case 'estoque':
      iframe.src = '../html/estoque.html';
      break;
    case 'veiculos':
      iframe.src = '../html/veiculo.html';
      break;
    case 'relatorios':
      iframe.src = '../html/relatorios.html';
      break;
    default:
      console.error(`Página '${pagina}' não encontrada.`);
  }
}
