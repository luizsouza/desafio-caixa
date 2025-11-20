# Painel de Investimentos – Desafio Caixa

> **Autor:** Luiz Carlos Mendes Souza (c158806-0)  
> **Objetivo:** API que simula investimentos, calcula perfil de risco dinâmico e recomenda produtos conforme o PSI Pleno/Sênior.

---

## 1. Visão geral

A API usa **Java 21 / Spring Boot 3.3**, com segurança **JWT**, banco **SQLite** e documentação **Swagger**. Fluxo principal:

1. Autenticar em `/auth/login` com `admin/senha123`.  
2. Simular investimentos e consultar histórico/métricas.  
3. Obter perfil de risco dinâmico, recomendações e telemetria.

---

## 2. Tecnologias

- Spring Boot (Web, Data JPA, Validation)
- Spring Security + JWT (jjwt 0.11)
- SQLite (dialeto Hibernate community)
- Swagger (springdoc-openapi)
- JUnit 5 / Mockito / JaCoCo
- Docker + Docker Compose

---

## 3. Como executar

```bash
git clone https://github.com/luizsouza/desafio-caixa.git
cd desafio-caixa

# Maven
mvn clean install
mvn spring-boot:run

# Docker
docker compose build
docker compose up
```

O SQLite versionado fica em `data/investimentos.db` (montado como volume para manter evidências).

---

## 4. Autenticação e Swagger

| Usuário | Senha    |
|---------|----------|
| admin   | senha123 |

1. `POST /auth/login` → recebe `{ "token": "...", "tipo": "Bearer" }`
2. Use `Authorization: Bearer <token>` nos demais endpoints.
3. Swagger em `http://localhost:8080/swagger-ui.html` (clique em **Authorize** e informe o token).

---

## 5. Endpoints e restrições

| Método | Caminho | Descrição | Restrições principais |
|--------|---------|-----------|-----------------------|
| POST | `/simular-investimento` | `{clienteId, valor, prazoMeses, tipoProduto}` → produto validado + resultado | Todos obrigatórios; `valor > 0`, `prazoMeses >= 1`; 404 se `tipoProduto` não existir. |
| GET | `/simulacoes` | Histórico completo das simulações | Mais recente primeiro; requer token. |
| GET | `/simulacoes/por-produto-dia` | Métricas agregadas por produto e dia | `inicio`/`fim` opcionais (AAAA-MM-DD); se ausentes, últimos 30 dias. |
| GET | `/perfil-risco/{clienteId}` | Calcula perfil dinâmico (pontuação e descrição) | Requer simulações prévias; senão 404 (`SemDadosPerfilException`). |
| GET | `/produtos-recomendados/{perfil}` | Motor de recomendação | Perfil conservador/moderado/agressivo (case-insensitive). |
| GET | `/investimentos/{clienteId}` | Histórico de investimentos persistidos | Ordenado por data desc. |
| GET | `/telemetria` | Volume e tempo médio por serviço | `inicio`/`fim` opcionais; fallback 30 dias. |
| GET | `/health` | Health-check público | Sem JWT. |

---

## 6. Como cada módulo funciona

### 6.1 Simulações
- Bean Validation nos requests.
- `SimulacaoService` busca o produto por tipo, calcula rentabilidade linear (rentabilidade anual × prazo em anos) e salva em `simulacoes` + histórico `investimentos_cliente`.
- Valores monetários são arredondados para 2 casas decimais na persistência e nas respostas.

### 6.2 Motor de recomendação
- `RecomendacaoService` exposto em `/produtos-recomendados/{perfil}`.
- Fluxo: cliente simula → perfil dinâmico em `/perfil-risco/{clienteId}` → recomendação por perfil.
- Regras/pontuação:  
  - Conservador: risco BAIXO; `pontuacao = rentabilidade * 0.8`  
  - Moderado: risco BAIXO ou MEDIO; `pontuacao = rentabilidade`  
  - Agressivo: qualquer risco; `pontuacao = rentabilidade * 1.2`
- Seeds (SQLite): CDB Caixa 2026 (BAIXO, 0.12), Fundo XPTO (MEDIO, 0.18), Ações Arrojadas (ALTO, 0.25). Ranking varia conforme o perfil calculado.

### 6.3 Perfil de risco dinâmico
- `PerfilRiscoService` soma Volume + Frequência + Liquidez:  
  - Volume: <10k=10; até 50k=20; >50k=30  
  - Frequência: até 2=10; até 5=20; >5=30  
  - Liquidez (prazo médio): <=6m=10; <=18m=20; >18m=30
- Total: <=40 Conservador; <=70 Moderado; >70 Agressivo. Retorna também as pontuações parciais.

### 6.4 Telemetria
- `TelemetriaInterceptor` mede duração de endpoints críticos.
- `TelemetriaService` agrupa por endpoint e retorna quantidade + tempo médio no período informado.
- `/telemetria` aceita `inicio`/`fim` e devolve lista de serviços + período.

### 6.5 Mapeamento de DTOs
- `DtoMapper` centraliza conversão entidade → DTO (produtos, investimentos, simulações, telemetria), reduzindo repetição. Usado por `ProdutoService`, `InvestimentoService`, `SimulacaoService`, `TelemetriaService` e `RecomendacaoService`.

### 6.6 Banco de dados
- `schema.sql` cria tabelas (`produtos`, `simulacoes`, `telemetria`, `investimentos_cliente`) e insere seeds (produtos e histórico do cliente 123).
- `application.properties` aponta para `jdbc:sqlite:./data/investimentos.db`.

---

## 7. Testes e evidências

- `mvn clean test` executa unitários e integrações.
- Relatório JaCoCo: `target/site/jacoco/index.html`.
- Análise estática: `mvn verify` roda SpotBugs + FindSecBugs (failOnError). O scan de CVEs via OWASP Dependency Check foi removido do build para evitar dependência de NVD/API na avaliação.
- Resumo da suíte:

| Classe | Tipo | Cenários cobertos |
|--------|------|-------------------|
| `SimulacaoServiceTest` | Unitário | Cálculo do valor final, persistência e exceções para produto desconhecido |
| `SimulacaoControllerIT` | Integração | POST `/simular-investimento` e GET `/simulacoes/por-produto-dia` com segurança configurada |
| `PerfilRiscoServiceTest` | Unitário | Perfis conservador/moderado/agressivo via pontuação de volume/frequência/liquidez |
| `RecomendacaoServiceTest` | Unitário | Filtro/ordenação/pontuação por perfil |
| `TelemetriaServiceTest` | Unitário | Agrupamento de tempos e quantidade por endpoint |
| `TelemetriaInterceptorTest` | Unitário | Monitoramento apenas de endpoints configurados e registro de duração |
| `TelemetriaEntityTest` | Unitário | Construtor e getters/setters da entidade de telemetria |
| `OpenApiConfigTest` | Unitário | Customizers de Swagger (exemplos de data e ordenação do /auth/login) |
| `ApiExceptionHandlerTest` / `ApiExceptionHandlerExtraTest` | Unitário | Mensagens e status para validação, corpo ilegível e parâmetros inválidos |
| `JwtServiceTest` / `JwtAuthenticationFilterTest` / `AuthControllerIT` | Unit./Integração | Geração/validação de token, filtros JWT e fluxo de login |
| `InvestimentoServiceTest` | Unitário | Conversão do histórico para DTO |
| `ProdutoRepositoryDataTest` | Integração (@DataJpaTest) | Seeds reais (SQLite) e busca por tipo |

Evidências extras: Swagger documenta contratos; Dockerfile/docker-compose garantem reprodutibilidade.

---

## 8. Validação

1. Clonar/descompactar o projeto.  
2. `docker compose build && docker compose up`.  
3. `POST /auth/login` → copiar token → usar endpoints no Swagger.  
4. Rodar `mvn clean test` e abrir `target/site/jacoco/index.html` para conferir cobertura.

Qualquer dúvida sobre o motor, perfil de risco ou arquitetura, estou à disposição. Obrigado pela avaliação! 💙
