CREATE TABLE pessoas (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    cpf VARCHAR(14) NOT NULL UNIQUE,
    numero VARCHAR(10) NOT NULL,
    complemento VARCHAR(100),
    cep VARCHAR(9) NOT NULL,
    bairro VARCHAR(100) NOT NULL,
    municipio VARCHAR(100) NOT NULL,
    estado VARCHAR(2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE auditoria (
    id SERIAL PRIMARY KEY,
    tipo_operacao VARCHAR(20) NOT NULL,
    entidade VARCHAR(50) NOT NULL,
    entidade_id BIGINT,
    dados_anteriores JSONB,
    dados_atuais JSONB,
    usuario VARCHAR(100),
    data_operacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);