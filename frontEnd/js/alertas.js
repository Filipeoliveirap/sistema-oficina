// Exibe um alerta de erro
function alertaErro(titulo = 'Erro', mensagem = 'Algo deu errado!') {
    Swal.fire({
        icon: 'error',
        title: titulo,
        text: mensagem,
        confirmButtonColor: '#d33'
    });
}

// Exibe um alerta de sucesso
function alertaSucesso(titulo = 'Sucesso!', mensagem = '') {
    return Swal.fire({
        icon: 'success',
        title: titulo,
        text: mensagem,
        confirmButtonColor: '#3085d6',
        confirmButtonText: 'OK',
    });
}

// Confirmação com retorno (ex: excluir cliente)
async function confirmarAcao(mensagem = 'Deseja realmente continuar?') {
    const resultado = await Swal.fire({
        title: 'Confirmação',
        text: mensagem,
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Sim',
        cancelButtonText: 'Cancelar'
    });
    return resultado.isConfirmed;
}

function alertaCpfExistente(cpf) {
    alertaErro(
        'CPF já cadastrado',
        `O cliente com CPF ${cpf} já possui cadastro no sistema.`
    );
}
