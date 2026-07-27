# Radiant Board - Project Context & Architecture

## 🎯 Visão Geral
O Radiant Board não é apenas um visualizador de partidas, mas uma **Ferramenta de Diagnóstico Clínico** (Motor de Heurísticas) para Valorant. O objetivo é ajudar o jogador a identificar erros de tomada de decisão (Over-peeks, mortes isoladas, ausência de trade kills) através da reprodução vetorial dos abates em uma prancheta tática interativa.

## 🛠️ Stack Tecnológico (Kotlin Multiplatform)
- **UI:** Compose Multiplatform (Android/iOS)
- **Injeção de Dependências:** Koin
- **Rede & API:** Ktor 3.x (Negociação de Conteúdo via `kotlinx.serialization`)
- **Imagens Assíncronas:** Kamel Image (`1.0.3`)
- **API Consumida:** HenrikDev Valorant API v3

## 🏗️ Padrões Arquiteturais
- **Clean Architecture:** Separação estrita entre `domain`, `network` (data) e `ui` (presentation).
- **Desnormalização em Memória:** Arrays complexos da API (como jogadores) são convertidos em Dicionários (`Map<String, MatchPlayerInfo>`) no Repositório para garantir consultas de tempo constante $O(1)$ na UI.
- **Unidirectional Data Flow (UDF):** O ViewModel expõe estados fechados (`MatchUiState`) imutáveis para a UI consumir.

## ✅ Estado Atual (O que já funciona)
1. **Engine Gráfica (Coordinate Mapper):** Matriz de conversão de coordenadas da Unreal Engine para o Canvas 2D (inversão de eixos X/Y) implementada com sucesso.
2. **Sistema de Replay (DVR):**
    - Partidas divididas em Rounds.
    - Slider temporal utilizando `kill_time_in_round`.
3. **Renderização Tática (BoxWithConstraints):**
    - Substituição do `Canvas` bruto pela composição de elementos dinâmicos.
    - Imagens dos Agentes renderizadas dinamicamente sobre o mapa.
    - **Hierarquia Visual / Z-Index:** O usuário logado é destacado em amarelo (tamanho e Z-index maiores). Aliados em azul, inimigos em vermelho.

## 🚧 Roadmap Imediato (Próximos Passos)
1. **Refatoração da Tubulação de Dados (Data Layer):**
    - Expandir `MatchDto` e `Kill` para capturar `killer_location` (coordenada do assassino) e `damage_weapon_name` (arma utilizada).
2. **Motor de Heurísticas (Domain Layer):**
    - Criar `MatchDiagnosticsEngine` (UseCase) para interpretar o contexto das mortes (ex: calcular distâncias vetoriais e cruzar tempo/arma para diagnosticar *Over-peek* ou *Trade Kill*).
3. **UI Interativa e Linhas de Visão:**
    - Desenhar vetores (linhas de tiro) entre `killer_location` e `victim_location`.
    - Adicionar interatividade aos ícones dos agentes (Cards de diagnóstico de abate).