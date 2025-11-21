------------------------------------------------------------
-- Inserção dos produtos iniciais quando a tabela está vazia
------------------------------------------------------------
INSERT INTO produtos (nome, tipo, rentabilidade, risco)
SELECT 'CDB Conservador', 'CDB', 0.8, 'Baixo'
WHERE NOT EXISTS (SELECT 1 FROM produtos);

INSERT INTO produtos (nome, tipo, rentabilidade, risco)
SELECT 'Fundo Moderado', 'FUNDO', 1.2, 'Médio'
WHERE NOT EXISTS (SELECT 1 FROM produtos WHERE tipo = 'FUNDO');

INSERT INTO produtos (nome, tipo, rentabilidade, risco)
SELECT 'Ações Arrojadas', 'AÇÕES', 2.0, 'Alto'
WHERE NOT EXISTS (SELECT 1 FROM produtos WHERE tipo = 'AÇÕES');
