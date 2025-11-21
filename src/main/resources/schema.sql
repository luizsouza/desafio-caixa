---------------------------------------------------------------------------
--  CRIAÇÃO DAS TABELAS DO SISTEMA PAINEL DE INVESTIMENTOS (DESAFIO CAIXA)
---------------------------------------------------------------------------

---------------------------------------------------------------------------
-- TABELA: PRODUTOS
---------------------------------------------------------------------------

DROP TABLE IF EXISTS produtos;

CREATE TABLE IF NOT EXISTS produtos (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    nome            TEXT NOT NULL UNIQUE,
    tipo            TEXT NOT NULL,
    rentabilidade   REAL NOT NULL,
    risco           TEXT NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_produtos_tipo ON produtos (tipo);

---------------------------------------------------------------------------
-- TABELA: SIMULAÇÕES
-- Cada chamada POST /simular-investimento gera um registro
-- "valorInvestido" deve ser persistido
---------------------------------------------------------------------------

DROP TABLE IF EXISTS simulacoes;

CREATE TABLE IF NOT EXISTS simulacoes (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    cliente_id      INTEGER NOT NULL,
    produto_id      INTEGER NOT NULL,
    valor_investido REAL NOT NULL,
    valor_final     REAL NOT NULL,
    prazo_meses     INTEGER NOT NULL,
    data_simulacao  TEXT NOT NULL,

    FOREIGN KEY (produto_id) REFERENCES produtos(id)
);

CREATE INDEX IF NOT EXISTS idx_simulacoes_cliente ON simulacoes (cliente_id);
CREATE INDEX IF NOT EXISTS idx_simulacoes_data    ON simulacoes (data_simulacao);

---------------------------------------------------------------------------
-- TABELA: TELEMETRIA
-- Cada requisição salva:
--   endpoint, tempo_resposta_ms, timestamp (ISO)
-- Telemetria retorna estatísticas por endpoint
---------------------------------------------------------------------------

DROP TABLE IF EXISTS telemetria;

CREATE TABLE IF NOT EXISTS telemetria (
    id                  INTEGER PRIMARY KEY AUTOINCREMENT,
    endpoint            TEXT NOT NULL,
    tempo_resposta_ms   INTEGER NOT NULL,
    timestamp           TEXT NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_telemetria_endpoint ON telemetria (endpoint);
CREATE INDEX IF NOT EXISTS idx_telemetria_ts       ON telemetria (timestamp);

---------------------------------------------------------------------------
-- TABELA: HISTÓRICO DE INVESTIMENTOS (ENDPOINT /investimentos/{clienteId})
---------------------------------------------------------------------------

DROP TABLE IF EXISTS investimentos_cliente;

CREATE TABLE IF NOT EXISTS investimentos_cliente (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    cliente_id      INTEGER NOT NULL,
    tipo            TEXT NOT NULL,
    valor           REAL NOT NULL,
    rentabilidade   REAL NOT NULL,
    data            DATE NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_investimentos_cliente ON investimentos_cliente (cliente_id);

---------------------------------------------------------------------------
-- PRODUTOS PADRÃO
---------------------------------------------------------------------------

INSERT INTO produtos (nome, tipo, rentabilidade, risco)
SELECT 'CDB Caixa 2026', 'CDB', 0.12, 'BAIXO'
WHERE NOT EXISTS (SELECT 1 FROM produtos WHERE nome = 'CDB Caixa 2026');

INSERT INTO produtos (nome, tipo, rentabilidade, risco)
SELECT 'Fundo XPTO', 'FUNDO', 0.18, 'MÉDIO'
WHERE NOT EXISTS (SELECT 1 FROM produtos WHERE nome = 'Fundo XPTO');

INSERT INTO produtos (nome, tipo, rentabilidade, risco)
SELECT 'Ações Arrojadas', 'AÇÕES', 0.25, 'ALTO'
WHERE NOT EXISTS (SELECT 1 FROM produtos WHERE nome = 'Ações Arrojadas');