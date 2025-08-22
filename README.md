# 📚 Aubos API

O **Aubos** é uma API RESTful para gerenciamento e acompanhamento de informações sobre livros e suas equipes criativas.  

O sistema permite registrar e consultar dados como autores, editores, tradutores e outros colaboradores de uma obra, incluindo períodos de participação (ex: Autor X: 2000–2005, Autor Y: 2005–ATUAL).

---

##  Arquitetura

- **Linguagem:** Java  
- **Framework:** Spring Boot  
- **Segurança:** Spring Security  
  - Autenticação via **API Key** para leitura  
  - Autenticação via **JWT Token** para escrita  
- **Banco de Dados:** MySQL  
- **Cache:** Redis (com suporte a ETag)  
- **Estilo:** RESTful

---

##  Autenticação

### 1. API Key (para leitura)

Obrigatória em todas as requisições públicas (**GET**).  

```http
GET /api/books/123
Authorization: ApiKey {SUA_API_KEY}
```

---

### 2. JWT Token (para escrita)

Necessário para criar, atualizar ou remover dados.

```http
POST /api/books
Authorization: Bearer {SEU_JWT_TOKEN}
```

---

##  Endpoints Principais

| Método   | Caminho           | Descrição                          |
| -------- | ----------------- | ---------------------------------- |
| `GET`    | `/api/books/{id}` | Detalhes de um livro específico    |
| `POST`   | `/api/books`      | Cria novo livro _(JWT necessário)_ |
| `PUT`    | `/api/books/{id}` | Atualiza livro _(JWT necessário)_  |
| `DELETE` | `/api/books/{id}` | Remove livro _(JWT necessário)_    |

---

## 📦 Exemplos de Resposta

### Sucesso

```json
{
  "status": 200,
  "path": "/api/books/123",
  "data": {
    "id": "123",
    "title": "Livro Exemplo",
    "publishedYear": 2000,
    "authors": [
      { "name": "Autor X", "from": "2000", "to": "2005" },
      { "name": "Autor Y", "from": "2005", "to": "ATUAL" }
    ],
    "publisher": "Editora Exemplo"
  },
  "actions": {
    "action": "/api/books/123",
    "action": "/api/books/123/authors"
  }
}
```

### Erro

```json
{
  "status": 404,
  "path": "/api/books/999",
  "message": "Livro não encontrado",
  "timestamp": "2025-08-22T19:45:00Z",
  "errors": null
}
```

---

## Cache e ETag

- As respostas **GET** podem conter **ETag**, permitindo cache eficiente.
    
- Exemplo de uso com `If-None-Match`:
    

```http
GET /api/books/123
Authorization: ApiKey {SUA_API_KEY}
If-None-Match: "abc123etag"
```

Resposta:

```http
304 Not Modified
```

---

##  Boas Práticas

- Sempre inclua a **API Key** em requisições públicas.
    
- Use **JWT Token** para operações de escrita.
    
- Utilize **paginação e filtros** em consultas grandes.
    
- Respeite os códigos HTTP retornados pela API (`200`, `201`, `404`, `409`, `422`, `500`).
    
- Aproveite o uso de `ETag` para otimizar consumo de dados.
    

---

##  Status do Projeto

 Em desenvolvimento

- ✅ Estrutura inicial da API
    
- ✅ Autenticação com API Key e JWT
    
-  ✅  Integração com MySQL e Redis
    
-  Criação de todas as endpoints
    
-  Documentação completa dos endpoints
    

