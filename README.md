## 🧾 EmiteAí - Cadastro de Pessoas

Projeto fullstack com backend em Spring Boot e frontend em React + MUI.

Permite o cadastro de pessoas físicas com validação de campos, geração de relatórios CSV assíncronos via RabbitMQ, e visualização por interface.

---

### 🚀 Como rodar o projeto

#### 1. Subir os containers do backend

```bash
docker compose down --volumes --remove-orphans \
  && docker compose build --no-cache \
  && docker compose up --force-recreate
```

> Isso vai subir RabbitMQ, PostgreSQL e outros serviços necessários.

#### 2. Em outro terminal, inicie o backend Spring Boot:

```bash
./mvnw spring-boot:run
```

#### 3. Rode o frontend React:

```bash
cd emiteai-frontend

touch .env && \
grep -q '^REACT_APP_API_URL=' .env && \
sed -i '' 's|^REACT_APP_API_URL=.*|REACT_APP_API_URL=http://localhost:8080|' .env || \
echo 'REACT_APP_API_URL=http://localhost:8080' >> .env

npm install
npm start
```

---

### 📦 Estrutura e funcionamento

* O **backend** espera requisições com os seguintes campos no corpo (`PessoaRequest.java`):

  * `nome` (obrigatório)
  * `telefone` (formato: (99) 99999-9999)
  * `cpf` (formato: 999.999.999-99)
  * `numero` (obrigatório)
  * `complemento` (opcional)
  * `cep` (formato: 99999-999)

Essas validações estão presentes no backend com `@NotBlank`, `@Pattern` e `@CPF`.

* O **frontend (`PersonForm.tsx`)** envia esses dados exatamente no formato exigido.

* Ao gerar um relatório, o frontend dispara um comando para o backend que envia uma mensagem para a fila `fila-relatorio-pessoas`. Um worker consome essa fila e cria o CSV.

---

### 📊 Observação

Para visualizar a fila no RabbitMQ, acesse:

```
http://localhost:15672
```

Login padrão:

* **Usuário:** `guest`
* **Senha:** `guest`

---