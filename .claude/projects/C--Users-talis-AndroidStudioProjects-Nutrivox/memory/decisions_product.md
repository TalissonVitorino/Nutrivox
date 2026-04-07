---
name: Product Decisions
description: Decisões de produto fechadas na revisão de requisitos - hierarquia, IA, metas, onboarding
type: project
---

Decisões fechadas em 2026-04-04:

1. **Hierarquia formal**: Paciente → Plano Alimentar (1 ativo) → Variação de Dieta (N) → Refeição → Item → Substituição
2. **IA não sugere chás para condições clínicas** — risco regulatório de fitoterápicos removido do escopo
3. **Metas nutricionais diárias** (kcal, P, C, G, fibras) são requisito de MVP
4. **Onboarding via convite do nutricionista** — nutricionista cadastra paciente, gera convite (link/código)
5. **Paciente pode registrar alimento fora do plano** — fica sinalizado como "fora do plano" para o nutricionista
6. **Substituição autorizada vs sugestão IA** — são entidades distintas com rastreabilidade separada
7. **Paciente alterna entre variações de dieta dentro do plano ativo**, nunca entre planos
8. **Registro de consumo com 4 modos**: total, parcial por item, com substituição autorizada, com alimento fora do plano
9. **Cor da IA sempre distinta** (azul/roxo) do tema do app
10. **Nomenclatura normalizada**: "variação de dieta" (não "dieta"), "item da refeição" (não "alimento por refeição"), "sugestão IA" (não "substituição da IA"), "registrar consumo" (não "registrar refeição")

**Why:** Eliminar ambiguidades da especificação original e fechar decisões antes da engenharia
**How to apply:** Toda documentação, design e implementação deve seguir essas decisões
