# Painel de Investimentos – Desafio Caixa

> **Autor:** Luiz Souza  
> **Objetivo:** disponibilizar uma API web que simula investimentos, ajusta o perfil de risco dinamicamente e recomenda produtos conforme os requisitos do PSI.

---

## 1. Visão geral

A API foi escrita em **Java 21 / Spring Boot 3.3**, protegida com **JWT**, persistindo em **SQLite** e empacotada com **Docker** para facilitar a avaliação.  
O fluxo principal é:

1. Autenticar via `/auth/login` usando `admin/senha123`.  
2. Gerar simulações e analisar métricas/histórico.  
3. Consultar perfil de risco, motor de recomendações e telemetria.

---

## 2. Tecnologias

- Spring Boot (Web, Data JPA, Validation)
- Spring Security + JWT (jjwt 0.11)
- SQLite (Hibernate community dialect)
- Swagger (springdoc-openapi)
- Mockito / JUnit / JaCoCo
- Docker + Docker Compose

---

## 3. Como executar

```bash
git clone https://github.com/luizsouza/desafio-caixa.git
cd desafio-caixa
```

### 3.1 Maven
```bash
mvn clean install
mvn spring-boot:run
```

### 3.2 Docker
```bash
docker compose build
docker compose up
```

O Banco SQLite (`data/investimentos.db`) está versionado e é montado como volume para manter as evidências.

---

## 4. Autenticação e Swagger

| Usuário | Senha    |
|---------|----------|
| admin   | senha123 |

1. `POST /auth/login` → `{ "token": "...", "tipo": "Bearer" }`
2. Use `Authorization: Bearer <token>` nos demais endpoints.
3. No Swagger (`http://localhost:8080/swagger-ui.html`), clique em **Authorize** e informe `Bearer <token>`.

---

## 5. Endpoints e restrições

| Método | Caminho | Descrição | Restrições principais |
|--------|---------|-----------|-----------------------|
| POST | `/simular-investimento` | Recebe `{clienteId, valor, prazoMeses, tipoProduto}`, valida e retorna produto + resultado | Todos os campos obrigatórios; `valor > 0`, `prazoMeses ≥ 1`. Gera erro 404 se o `tipoProduto` não existir. |
| GET | `/simulacoes` | Histórico completo das simulações | Ordenado do mais recente para o mais antigo; requer token. |
| GET | `/simulacoes/por-produto-dia` | Métricas agregadas por produto e dia | Param `inicio`/`fim` opcionais (AAAA-MM-DD). Se ausentes, período = últimos 30 dias. |
| GET | `/perfil-risco/{clienteId}` | Calcula perfil dinâmico (pontuação e descrição) | Requer simulações prévias; caso contrário retorna 404 (`SemDadosPerfilException`). |
| GET | `/produtos-recomendados/{perfil}` | Motor de recomendação | `perfil` deve ser conservador/moderado/agressivo (case-insensitive). |
| GET | `/investimentos/{clienteId}` | Histórico de investimentos persistidos | Ordenado por data desc. |
| GET | `/telemetria` | Volume e tempo médio por serviço | `inicio`/`fim` opcionais; fallback 30 dias. |
| GET | `/health` | Health-check público | Sem JWT. |

---

## 6. Como cada módulo funciona

### 6.1 Simulações
- Validação via Bean Validation.
- `SimulacaoService` busca o produto por tipo, calcula rentabilidade linear (rentabilidade anual × prazo em anos), salva em `simulacoes` e no histórico `investimentos_cliente`.
- O response segue o JSON proposto pela banca.

### 6.2 Motor de recomendacao
- Implementado em `RecomendacaoService` e exposto em `/produtos-recomendados/{perfil}`.
- Como se conecta ao perfil dinâmico:
  1) O cliente faz simulações via `/simular-investimento`; cada chamada salva o produto e prazo em `simulacoes`.
  2) `/perfil-risco/{clienteId}` soma volume + frequência + liquidez e devolve o perfil calculado (CONSERVADOR/MODERADO/AGRESSIVO).
  3) Esse perfil é usado como entrada em `/produtos-recomendados/{perfil}`, que filtra produtos permitidos e calcula uma pontuação.
- Regras de compatibilidade e pontuação:
  - Conservador: aceita apenas risco BAIXO; `pontuacao = rentabilidade * 0.8`.
  - Moderado: aceita risco BAIXO ou MEDIO; `pontuacao = rentabilidade`.
  - Agressivo: aceita qualquer risco; `pontuacao = rentabilidade * 1.2`.
- Produtos seeds (SQLite):

  | Nome            | Tipo  | Risco | Rentabilidade (aa) |
  |-----------------|-------|-------|--------------------|
  | CDB Caixa 2026  | CDB   | BAIXO | 12% (0.12)         |
  | Fundo XPTO      | FUNDO | MEDIO | 18% (0.18)         |
  | Acoes Arrojadas | ACOES | ALTO  | 25% (0.25)         |

- Exemplos de ranking com esses dados:
  - Conservador: apenas o CDB entra e pontua `0.12 * 0.8 = 0.096`.
  - Moderado: CDB `0.12`, Fundo `0.18`; o Fundo fica em primeiro.
  - Agressivo: todos entram; Acoes ficam em primeiro com `0.25 * 1.2 = 0.30`, depois Fundo (`0.18 * 1.2 = 0.216`), depois CDB (`0.12 * 1.2 = 0.144`).
- Resultado: o mesmo catalogo de produtos muda de ordem conforme o perfil calculado a partir do comportamento real do cliente (simulacoes).

### 6.3 Perfil de risco dinâmico
- `PerfilRiscoService` usa todas as simulações do cliente.
- Pontuação = Volume + Frequência + Liquidez:  
  - Volume: <10k = 10 pts, até 50k = 20, >50k = 30  
  - Frequência: até 2 = 10, até 5 = 20, acima de 5 = 30  
  - Liquidez (prazo médio): ≤6m = 10, ≤18m = 20, >18m = 30
- Total <=40 → Conservador; <=70 → Moderado; >70 → Agressivo.  
  O endpoint retorna também as pontuações parciais para dar transparência.

### 6.4 Telemetria
- `TelemetriaInterceptor` captura duração de endpoints críticos.
- `TelemetriaService` agrupa por endpoint e calcula quantidade + tempo médio no período informado.  
- `/telemetria` aceita `inicio`/`fim` e retorna um DTO com lista de serviços + período.

### 6.5 Banco de dados
- `schema.sql` cria as tabelas (`produtos`, `simulacoes`, `telemetria`, `investimentos_cliente`) e insere dados padrão (produtos, histórico do cliente 123).  
- `application.properties` aponta para `jdbc:sqlite:./data/investimentos.db`.

---

## 7. Testes e evidências

- `mvn clean test` executa unitários e integrações.
- Relatório JaCoCo em `target/site/jacoco/index.html`.
- Resumo da suíte:

| Classe | Tipo | Cenários cobertos |
|--------|------|-------------------|
| `SimulacaoServiceTest` | Unitário | Cálculo do valor final, persistência e exceções para produto desconhecido |
| `SimulacaoControllerIT` | Integração | POST `/simular-investimento` e GET `/simulacoes/por-produto-dia` com segurança configurada |
| `PerfilRiscoServiceTest` | Unitário | Pontuação por volume/frequência/liquidez e definição de perfil |
| `RecomendacaoServiceTest` | Unitário | Filtro/ordenação do motor de recomendação |
| `TelemetriaServiceTest` | Unitário | Agrupamento de tempos e quantidade por endpoint |
| `JwtServiceTest` / `AuthControllerIT` | Unit./Integração | Geração/validação de token e fluxo de login |
| `InvestimentoServiceTest` | Unitário | Conversão do histórico para DTO |

Os testes garantem comportamento real (payloads, regras, segurança).  
Evidências adicionais: Swagger documenta contratos; Dockerfile/docker-compose permitem reproducibilidade.

---

## 9. Validação

1. Clonar/descompactar o projeto.
2. `docker compose build && docker compose up`.
3. `POST /auth/login` → copiar token → usar endpoints no Swagger.
4. (Opcional) Rodar `mvn clean test` e abrir `target/site/jacoco/index.html` para conferir cobertura.

Qualquer dúvida sobre o motor, perfil de risco ou arquitetura, estou à disposição. Obrigado pela avaliação! 💙