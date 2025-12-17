-- Criação da tabela Cliente
CREATE TABLE Cliente (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nome TEXT NOT NULL,
    cpf TEXT NOT NULL UNIQUE,
    telefone TEXT,
    email TEXT,
    endereco TEXT
);

-- Criação da tabela Produto
CREATE TABLE Produto (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nome TEXT NOT NULL,
    quantidade INTEGER NOT NULL,
    precoUnitario REAL NOT NULL,
    categoria TEXT,
    observacao TEXT
);

-- Criação da tabela Servico
CREATE TABLE Servico (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    descricao TEXT NOT NULL,
    preco REAL NOT NULL,
    data TEXT NOT NULL,
    cliente_id INTEGER NOT NULL,
    FOREIGN KEY (cliente_id) REFERENCES Cliente(id)
);

-- Tabela intermediária para relacionamento N:M entre Servico e Produto
CREATE TABLE Servico_Produto (
    servico_id INTEGER NOT NULL,
    produto_id INTEGER NOT NULL,
    PRIMARY KEY (servico_id, produto_id),
    FOREIGN KEY (servico_id) REFERENCES Servico(id),
    FOREIGN KEY (produto_id) REFERENCES Produto(id)
);

-- Criação da tabela ServicoFinalizado
CREATE TABLE ServicoFinalizado (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    descricao TEXT NOT NULL,
    preco REAL NOT NULL,
    dataInicio TEXT NOT NULL,
    dataFinalizacao TEXT NOT NULL,
    nomeCliente TEXT NOT NULL,
    cpfCliente TEXT NOT NULL,
    observacoes TEXT,
    cliente_id INTEGER,
    FOREIGN KEY (cliente_id) REFERENCES Cliente(id)
);
