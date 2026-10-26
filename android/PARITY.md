# Hermex Android parity roadmap

Statuses: `done` means this Android slice is implemented; `partial` means only the listed first-slice path exists; `todo` means not ported; `n/a` means the iOS-only capability does not have an Android equivalent in scope.

| Feature area | Upstream Swift path | Android status | Kotlin owner |
|---|---|---|---|
| Chat | `HermesMobile/Features/Chat/` | partial — send, token stream, stop | `app/src/main/kotlin/com/sdelcore/hermex/MainActivity.kt` |
| Sessions | `HermesMobile/Features/SessionList/` | partial — list and open | `app/src/main/kotlin/com/sdelcore/hermex/MainActivity.kt` |
| Models | `HermesMobile/Models/ServerCatalog.swift` | todo | — |
| Profiles & projects | `HermesMobile/Features/Profiles/`, `HermesMobile/Features/Projects/` | todo | — |
| Memory | `HermesMobile/Features/Memory/` | todo | — |
| Tasks/Kanban | `HermesMobile/Features/Tasks/`, `HermesMobile/Features/Kanban/` | todo | — |
| Skills | `HermesMobile/Features/Skills/` | todo | — |
| Insights | `HermesMobile/Features/Insights/` | todo | — |
| Bots | `HermesMobile/Features/Bots/` | todo | — |
| Settings | `HermesMobile/Features/Settings/` | partial — base URL/token and connection probe | `app/src/main/kotlin/com/sdelcore/hermex/MainActivity.kt` |
| Onboarding | `HermesMobile/Features/Onboarding/` | partial — connection form | `app/src/main/kotlin/com/sdelcore/hermex/MainActivity.kt` |
| Share | `HermesShareExtension/` | n/a — Android shares through intents, no iOS extension | — |
| MoveToHermes | `HermesMobile/Features/MoveToHermes/` | todo | — |
| Workspace | `HermesMobile/Features/Workspace/` | todo | — |
| Push notifications | `HermesMobile/Push/` | todo | — |
| Live activities | `HermesMobile/LiveActivities/`, `HermesLiveActivityWidget/` | n/a — iOS ActivityKit/WidgetKit surfaces | — |
| App Intents | `HermesMobile/AppIntents/` | n/a — iOS App Intents API | — |
## Upstream sync through `c6cb3a95`

- #1337 currency/inline-math boundary fix: not applicable to the current Android chat surface. Android renders streamed response text literally with Compose `Text`; it has no inline-math parser or TeX renderer, so currency delimiters cannot pair into math spans.
- #1336 separate-gateway session edits: not applicable to the current Android protocol. `HermesProtocol` targets the legacy webui `/api/sessions` and `/api/chat/*` routes; it has no direct-Hermes gateway session creation, profile-note setup, or session mutation calls. Do not send the Hermes `gui` source or map gateway-specific refusal errors onto the webui contract.
- Revisit both ports if Android adds inline-math rendering or the direct-Hermes gateway surface.
