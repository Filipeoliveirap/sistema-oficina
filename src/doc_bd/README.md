# Database - Mechanical Workshop Management System

This document describes the database modeling used in the desktop management system for a mechanical workshop. The database was implemented using **SQLite** and mapped with **JPA / Hibernate** in the backend with **Spring Boot**.

## Entities and Relationships

### 🧍 Client
- `id` (PK)
- `name`
- `cpf` (tax ID)
- `phone`
- `email`
- `address`

**Relationships:**
- One client can have multiple associated services (`@OneToMany` with `Service`)
- A client can be linked to a finalized service (`@ManyToOne` with `FinishedService`)

---

### 🔧 Service
- `id` (PK)
- `description`
- `price`
- `date`
- `client_id` (FK)

**Relationships:**
- Many services belong to one client (`@ManyToOne`)
- A service can use multiple products (`@ManyToMany` with `Product`)

---

### 📦 Product
- `id` (PK)
- `name`
- `quantity`
- `unitPrice`
- `category`
- `note`

**Relationships:**
- Can be related to multiple services (`@ManyToMany` with `Service`)

---

### ✅ FinishedService
- `id` (PK)
- `description`
- `price`
- `startDate`
- `finishDate`
- `clientName`
- `clientCpf`
- `notes`
- `client_id` (optional FK)

**Notes:**
- Stores the history of completed services
- Does not participate in `@ManyToMany` relationship with `Product` as it is a consolidated record

---


## Technical Considerations

- All entities use `@GeneratedValue(strategy = GenerationType.IDENTITY)` for auto-increment.
- Validations were applied with `jakarta.validation` on entities (`@NotBlank`, `@Pattern`, `@DecimalMin`, etc.)
- Relationships were organized according to integrity and normalization requirements.

---

## Folder where this document is located

This README file and the ER model are located at:  
📁 `sistema-oficina/doc_bd/`


---

# Banco de Dados - Sistema de Gerenciamento de Oficina Mecânica

Este documento descreve a modelagem do banco de dados utilizada no sistema desktop de gerenciamento de uma oficina mecânica. O banco foi implementado utilizando **SQLite** e mapeado com **JPA / Hibernate** no backend com **Spring Boot**.

## Entidades e Relacionamentos

### 🧍 Cliente
- `id` (PK)
- `nome`
- `cpf`
- `telefone`
- `email`
- `endereco`

**Relacionamentos:**
- Um cliente pode ter vários serviços associados (`@OneToMany` com `Servico`)
- Um cliente pode estar vinculado a um serviço finalizado (`@ManyToOne` com `ServicoFinalizado`)

---

### 🔧 Servico
- `id` (PK)
- `descricao`
- `preco`
- `data`
- `cliente_id` (FK)

**Relacionamentos:**
- Muitos serviços pertencem a um cliente (`@ManyToOne`)
- Um serviço pode usar vários produtos (`@ManyToMany` com `Produto`)

---

### 📦 Produto
- `id` (PK)
- `nome`
- `quantidade`
- `precoUnitario`
- `categoria`
- `observacao`

**Relacionamentos:**
- Pode estar relacionado a vários serviços (`@ManyToMany` com `Servico`)

---

### ✅ ServicoFinalizado
- `id` (PK)
- `descricao`
- `preco`
- `dataInicio`
- `dataFinalizacao`
- `nomeCliente`
- `cpfCliente`
- `observacoes`
- `cliente_id` (FK opcional)

**Observações:**
- Armazena o histórico de serviços concluídos
- Não participa do relacionamento `@ManyToMany` com `Produto`, pois é um registro consolidado

---


## Considerações Técnicas

- Todas as entidades usam `@GeneratedValue(strategy = GenerationType.IDENTITY)` para autoincremento.
- Validações foram aplicadas com `jakarta.validation` nas entidades (`@NotBlank`, `@Pattern`, `@DecimalMin`, etc.)
- Os relacionamentos foram organizados de acordo com a necessidade de integridade e normalização.

---

## Pasta onde está este documento

Este arquivo README e o modelo ER estão localizados em:  
📁 `sistema-oficina/doc_bd/`
