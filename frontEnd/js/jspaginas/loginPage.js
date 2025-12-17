// loginPage.js

// Usuário e senha fixos
const usuarioValido = "admin";
const senhaValida = "123";

function alertaErro(titulo = 'Erro', mensagem = 'Algo deu errado!') {
  Swal.fire({
    icon: 'error',
    title: titulo,
    text: mensagem,
    confirmButtonColor: '#d33'
  });
}

function alertaSucesso(titulo = 'Sucesso!', mensagem = '') {
  return Swal.fire({
    icon: 'success',
    title: titulo,
    text: mensagem,
    confirmButtonColor: '#3085d6',
    confirmButtonText: 'OK',
  });
}

document.getElementById('loginForm').addEventListener('submit', function(e) {
  e.preventDefault();

  const username = document.getElementById('username').value.trim();
  const password = document.getElementById('password').value;

  if (username !== usuarioValido) {
    alertaErro('Usuário inválido', 'O usuário informado não existe.');
    return;
  }

  if (password !== senhaValida) {
    alertaErro('Senha incorreta', 'A senha digitada está errada.');
    return;
  }

  // Login OK, redirecionar para a página inicial
  window.location.href = 'index.html';
});
