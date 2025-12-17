document.addEventListener('DOMContentLoaded', () => {
    const tabelaClientes = document.getElementById('tabela-clientes');
    const adicionarClienteBtn = document.getElementById('adicionar-cliente-btn');
    const modal = document.getElementById('cadastro-cliente-modal');
    const closeBtn = document.querySelector('.close-button');
    const cancelarBtn = document.getElementById('cancelar-btn');
    const cadastroClienteForm = document.getElementById('cadastro-cliente-form');
    const pesquisarBtn = document.getElementById('pesquisar-btn');
    const params = new URLSearchParams(window.location.search);
    const clienteId = params.get('id');

    if (adicionarClienteBtn && cadastroClienteForm) {
        adicionarClienteBtn.addEventListener('click', () => {
        cadastroClienteForm.reset();
        document.getElementById('id').value = '';
        document.getElementById('titulo-formulario').innerText = 'Cadastro de Cliente';
        limparErrosFormulario();
    });
}

    if (closeBtn) {
        closeBtn.addEventListener('click', () => {
            modal.style.display = "none";
        });
    }

    if (cancelarBtn) {
        cancelarBtn.addEventListener('click', () => {
            window.location.href = 'clientes.html';
        });
    }

    window.addEventListener('click', (event) => {
        if (event.target === modal) {
            modal.style.display = "none";
        }
    });

    if (cadastroClienteForm) {
        cadastroClienteForm.addEventListener('submit', async function (event) {
            event.preventDefault();
            limparErrosFormulario();

            const id = document.getElementById('id').value;
            const nome = document.getElementById('nome').value.trim();
            const cpf = document.getElementById('cpf').value.trim();
            const telefone = document.getElementById('telefone').value.trim();
            const email = document.getElementById('email').value.trim();
            const endereco = document.getElementById('endereco').value.trim();

            let hasErrors = false;

            if (!nome) {
                formErrorResponse('nome-error', 'Nome é obrigatório');
                hasErrors = true;
            }
            if (!cpf) {
                formErrorResponse('cpf-error', 'CPF é obrigatório');
                hasErrors = true;
            }
            if (!telefone) {
                formErrorResponse('telefone-error', 'Telefone é obrigatório');
                hasErrors = true;
            }
            if (!email) {
                formErrorResponse('email-error', 'Email é obrigatório');
                hasErrors = true;
            } else if (!isValidEmail(email)) {
                formErrorResponse('email-error', 'Email inválido');
                hasErrors = true;
            }
            if (!endereco) {
                formErrorResponse('endereco-error', 'Endereço é obrigatório');
                hasErrors = true;
            }

            if (hasErrors) {
                await alertaErro('Erro no formulário', 'Por favor, corrija os campos destacados.');
                return;
            }

            const cliente = {
                nome,
                cpf,
                telefone,
                email,
                endereco: endereco || null
            };

            try {
                let response;
                if (id) {
                    response = await fetch(`http://localhost:8080/clientes/${id}`, {
                        method: 'PUT',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify(cliente)
                    });
                } else {
                    response = await fetch('http://localhost:8080/clientes', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify(cliente)
                    });
                }

                if (!response.ok) {
                    const errorData = await response.json();
                    const erroMsg = errorData.erro || 'Erro ao salvar/atualizar o cliente';

                    // Alerta específico para CPF duplicado
                    if (erroMsg.toLowerCase().includes('cpf duplicado')) {
                        formErrorResponse('cpf-error', 'Este CPF já está cadastrado.');
                        await alertaErro('CPF duplicado', 'Já existe um cliente cadastrado com este CPF.');
                        return;
                    }

                    throw new Error(erroMsg);
                }

                const clienteSalvo = await response.json();
                await alertaSucesso(id ? 'Cliente atualizado com sucesso!' : 'Cliente cadastrado com sucesso!');

                cadastroClienteForm.reset();
                modal.style.display = "none";
                window.location.href = 'clientes.html';

            } catch (error) {
                console.error('Erro:', error.message);
                await alertaErro('Erro ao salvar cliente', error.message);
                formErrorResponse('form-error', error.message);
            }
        });
    }

    if (pesquisarBtn) {
        pesquisarBtn.addEventListener('click', () => {
            const termoPesquisa = document.getElementById('pesquisar-cliente').value.toLowerCase();
            const linhas = tabelaClientes.querySelectorAll('tbody tr');

            linhas.forEach(linha => {
                const nome = linha.querySelector('td:nth-child(1)').textContent.toLowerCase();
                const email = linha.querySelector('td:nth-child(2)').textContent.toLowerCase();
                const telefone = linha.querySelector('td:nth-child(3)').textContent.toLowerCase();

                linha.style.display = (nome.includes(termoPesquisa) || email.includes(termoPesquisa) || telefone.includes(termoPesquisa))
                    ? ''
                    : 'none';
            });
        });
    }

    if (clienteId) {
        // Modo de edição
        document.getElementById('titulo-formulario').innerText = 'Editar Cliente';

        fetch(`http://localhost:8080/clientes/${clienteId}`)
            .then(res => {
                if (!res.ok) throw new Error('Falha ao buscar cliente');
                return res.json();
            })
            .then(cliente => {
                document.getElementById('id').value = cliente.id;
                document.getElementById('nome').value = cliente.nome;
                document.getElementById('cpf').value = cliente.cpf;
                document.getElementById('telefone').value = cliente.telefone;
                document.getElementById('email').value = cliente.email;
                document.getElementById('endereco').value = cliente.endereco;
            })
            .catch(async err => {
                console.error('Erro ao carregar cliente:', err);
                await alertaErro('Erro ao carregar cliente', 'Não foi possível carregar os dados do cliente.');
            });
    }
});

// Função para mostrar a mensagem de erro no campo
function formErrorResponse(inputID, mensagem) {
    const inputElement = document.getElementById(inputID);
    if (inputElement) {
        inputElement.textContent = mensagem;
    }
}

// Limpa todas as mensagens de erro do formulário
function limparErrosFormulario() {
    const idsErros = ['nome-error', 'cpf-error', 'telefone-error', 'email-error', 'endereco-error', 'form-error'];
    idsErros.forEach(id => {
        const el = document.getElementById(id);
        if (el) el.textContent = '';
    });
}

// Função para validar o e-mail
function isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

// Função para adicionar cliente na tabela
function adicionarClienteNaTabela(cliente) {
    const tabelaElement = document.getElementById('tabela-clientes');
    if (!tabelaElement) return;

    const tabela = tabelaElement.querySelector('tbody');
    const novaLinha = tabela.insertRow();
    novaLinha.setAttribute('data-id', cliente.id);
    novaLinha.innerHTML = `
        <td class="px-5 py-5 border-b border-gray-700 text-sm text-white">${cliente.nome}</td>
        <td class="px-5 py-5 border-b border-gray-700 text-sm text-white">${cliente.cpf}</td>
        <td class="px-5 py-5 border-b border-gray-700 text-sm text-white">${cliente.telefone}</td>
        <td class="px-5 py-5 border-b border-gray-700 text-sm text-white">${cliente.email}</td>
        <td class="px-5 py-5 border-b border-gray-700 text-sm text-white">
            ${cliente.endereco && cliente.endereco.trim() !== "" ? cliente.endereco : 'Não disponível'}
        </td>
        <td class="px-5 py-5 border-b border-gray-700 text-sm text-white text-center">
            <div class="flex justify-center items-center space-x-2">
                <button
                    class="bg-orange-600 hover:bg-orange-700 text-white font-semibold py-1 px-3 rounded-md shadow-md transition-colors text-xs"
                    onclick="editarCliente(${cliente.id})"
                    aria-label="Editar cliente ${cliente.nome}"
                >
                    <i class="fas fa-edit"></i> Editar
                </button>
                <button
                    class="bg-red-600 hover:bg-red-700 text-white font-semibold py-1 px-3 rounded-md shadow-md transition-colors text-xs"
                    onclick="excluirCliente(${cliente.id})"
                    aria-label="Excluir cliente ${cliente.nome}"
                >
                    <i class="fas fa-trash-alt"></i> Excluir 
                </button>
            </div>
        </td>
    `;
    

}
