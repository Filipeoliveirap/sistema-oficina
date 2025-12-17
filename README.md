# 🛠️ Workshop Management System

This is a **complete desktop application** for managing a mechanical workshop. The project evolved from a basic CRUD, now featuring modern technologies to provide a realistic and professional experience.

![Plataforma](https://img.shields.io/badge/platform-Desktop-blue)
![Backend](https://img.shields.io/badge/backend-Java%20%7C%20Spring%20Boot-orange)
![Frontend](https://img.shields.io/badge/frontend%20%20%7C%20JavaScript-green)
![Status](https://img.shields.io/badge/status-%20Completed-green)

---

## 📸 Demo

🎥 Watch the system running in the video below:  
👉 [YouTube Video Link](https://youtu.be/5qVeFsw1Pws)

---

## ✨ Features

- Client, service, and product registration
- Automatic inventory control and updates
- Service history per client
- PDF report generation with filters (name, CPF, date, etc.)
- Search finalized services by period, client, or description
- Modern and responsive UI with Electron
- Full integration between frontend and backend
- Error handling and validations

---

## 🧰 Technologies Used

### 🔙 Backend
- Java 17
- Spring Boot
- SQLite
- JPA / Hibernate

### 🔜 Frontend
- Electron
- HTML, CSS, JavaScript
- Tailwind CSS
- Fetch API

## 🗂️ Database Documentation

You can find the database documentation (ER diagram and SQL script) in the folder:  
📁 `src/doc_bd/`

---


## 🚀 How to Run the Project

### 1. Backend – Spring Boot

**Linux/macOS:**
```bash
./mvnw spring-boot:run
```

- Windows (Prompt PowerShell):
```bash
mvnw.cmd spring-boot:run
```

2. The backend will run at: [http://localhost:8080](http://localhost:8080).

### 2. Frontend (React/Node.js)

1. Frontend – Electron
```bash
cd frontEnd
```

2. 
```bash
npm install
npm start
```

3. The frontend will open automatically, usually at [http://localhost:3000](http://localhost:3000).

---

### Observations

- Make sure the backend is running before starting the frontend, so that the API is available for requests.
- If you need to change the port or other settings, check the `application.properties` file in the backend and the configuration files in the frontend.



---



# Sistema de Gerenciamento de Oficina Mecânica

Este é um sistema desktop para gerenciamento de oficina mecânica. O backend é desenvolvido em Java com Spring Boot e o frontend utiliza Electron, oferecendo uma experiência moderna e funcional para usuários.

![Plataforma](https://img.shields.io/badge/platform-Desktop-blue)
![Backend](https://img.shields.io/badge/backend-Java%20%7C%20Spring%20Boot-orange)
![Frontend](https://img.shields.io/badge/frontend%20%20%7C%20JavaScript-green)
![Status](https://img.shields.io/badge/status-%20Concluído-green)

---

## 📸 Demo

🎥 Assista o sistema rodando no link abaixo:  
👉 [YouTube Video Link](https://youtu.be/5qVeFsw1Pws)

## Funcionalidades

- Cadastro de clientes, serviços e produtos
- Controle de estoque
- Histórico de serviços realizados por cliente
- Geração de relatórios em PDF
- Busca e filtros dinâmicos
- Validações e tratamento de erros
- Integração completa entre frontend (Electron) e backend (Spring Boot + SQLite)

## Tecnologias Utilizadas

### Backend
- Java 17
- Spring Boot
- SQLite
- JPA / Hibernate

### Frontend
- Electron
- HTML/CSS/JavaScript
- Tailwind CSS
- Fetch API 

## 🗂️ Database documentação

Voce pode achar a documentação do banco de dados (ER diagrama e SQL script) na pasta:  
📁 `src/doc_bd/`

# Instruções para Executar o Projeto Completo

## Como Executar

### 1. Rodando o Backend (Spring Boot)

1. Execute o backend com o Maven Wrapper:

- No Linux/macOS:
```bash
./mvnw spring-boot:run
```

- No Windows (Prompt de Comando ou PowerShell):
```bash
mvnw.cmd spring-boot:run
```

2. Aguarde até o backend iniciar. Por padrão, ele estará disponível em [http://localhost:8080](http://localhost:8080).

---

### 2. Rodando o Frontend (React/Node.js)

1. Abra um novo terminal e navegue até o diretório do frontend (por exemplo, `frontEnd`):

```bash
cd frontEnd
```

2. Instale as dependências do frontend (só precisa rodar uma vez):

```bash
npm install
```

3. Inicie o servidor de desenvolvimento do frontend:

```bash
npm start
```

4. O frontend abrirá automaticamente, normalmente em [http://localhost:3000](http://localhost:3000).

---

### Observações

- Certifique-se de que o backend esteja rodando antes de iniciar o frontend, para que a API esteja disponível para as requisições.
- Caso precise alterar a porta ou outras configurações, veja os arquivos `application.properties` no backend e os arquivos de configuração do frontend.

