const apiUrl = "http://localhost:8080/produtos"; // Ajuste conforme seu backend

const form = document.getElementById('cadastro-estoque-form');
const idInput = document.getElementById('id');
const produtoInput = document.getElementById('produto');
const quantidadeInput = document.getElementById('quantidade');
const precoUnitarioInput = document.getElementById('precoUnitario');
const categoriaInput = document.getElementById('categoria');
const observacoesInput = document.getElementById('observacoes');
const cancelarBtn = document.getElementById('cancelar-btn');

function limparFormulario() {
    idInput.value = '';
    produtoInput.value = '';
    quantidadeInput.value = '';
    precoUnitarioInput.value = '';
    categoriaInput.value = '';
    observacoesInput.value = '';
}

// Funções para exibir alertas (pode customizar depois para modal/toast)
async function alertaSucesso(mensagem) {
    alert(mensagem);
}

async function alertaErro(titulo, mensagem) {
    alert(`${titulo}\n${mensagem}`);
}

async function salvarProduto(event) {
    event.preventDefault();

    const id = idInput.value;
    const produtoData = {
        id: id ? Number(id) : undefined,
        nome: produtoInput.value.trim(),
        quantidade: Number(quantidadeInput.value),
        precoUnitario: Number(precoUnitarioInput.value),
        categoria: categoriaInput.value.trim(),
        observacao: observacoesInput.value.trim()
    };

    try {
        let res;
        if (id) {
            // Editar
            res = await fetch(`${apiUrl}/${id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(produtoData)
            });
        } else {
            // Criar novo
            res = await fetch(apiUrl, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(produtoData)
            });
        }

        if (!res.ok) throw new Error('Erro ao salvar produto');

        await res.json(); // Produto salvo, retorno não usado aqui
        limparFormulario();

        await alertaSucesso('Produto salvo com sucesso!');
        window.location.href = 'estoque.html'; 
    } catch (error) {
        console.error(error);
        await alertaErro("Erro ao salvar produto", error.message);
    }
}

async function editarProduto(id) {
    try {
        const res = await fetch(`${apiUrl}/${id}`);
        if (!res.ok) throw new Error('Produto não encontrado');
        const produto = await res.json();

        idInput.value = produto.id;
        produtoInput.value = produto.nome;
        quantidadeInput.value = produto.quantidade;
        precoUnitarioInput.value = produto.precoUnitario;
        categoriaInput.value = produto.categoria || '';
        observacoesInput.value = produto.observacao || '';
        window.scrollTo({ top: 0, behavior: 'smooth' });
    } catch (err) {
        console.error(err);
        await alertaErro("Erro ao carregar produto para edição", err.message);
    }
}

async function excluirProduto(id) {
    const confirmado = confirm("Tem certeza que deseja excluir este produto?");
    if (!confirmado) return;

    try {
        const res = await fetch(`${apiUrl}/${id}`, { method: 'DELETE' });
        if (!res.ok) throw new Error('Erro ao excluir produto');

        await alertaSucesso('Produto excluído com sucesso!');
        window.location.reload(); // Atualiza a página ou redireciona conforme seu fluxo
    } catch (error) {
        console.error(error);
        await alertaErro("Erro ao excluir produto", error.message);
    }
}

// Função auxiliar para obter parâmetros da URL
function getQueryParam(param) {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get(param);
}

// Se houver um ID na URL, carregar o produto para edição
window.addEventListener('DOMContentLoaded', async () => {
    const id = getQueryParam('id');
    if (id) {
        try {
            const res = await fetch(`${apiUrl}/${id}`);
            if (!res.ok) throw new Error('Produto não encontrado');
            const produto = await res.json();

            idInput.value = produto.id;
            produtoInput.value = produto.nome;
            quantidadeInput.value = produto.quantidade;
            precoUnitarioInput.value = produto.precoUnitario;
            categoriaInput.value = produto.categoria || '';
            observacoesInput.value = produto.observacao || '';
        } catch (error) {
            console.error(error);
            await alertaErro("Erro ao carregar dados do produto para edição", error.message);
        }
    }
});

// Eventos
form.addEventListener('submit', salvarProduto);
cancelarBtn.addEventListener('click', (event) => {
    event.preventDefault();
    window.location.href = 'estoque.html';
});
