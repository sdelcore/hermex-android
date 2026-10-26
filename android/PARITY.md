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
| Bots | `HermesMobile/Features/Bots/` | todo — upstream c08c398 follows host-replaced Bot Chats, but Android has no Bots inbox, canonical Bot Chat target, Hermes gateway conversation attach, or cached Bot Chat transcript to follow; no equivalent behavior can be implemented on the existing Android surfaces | — |
| Settings | `HermesMobile/Features/Settings/` | partial — base URL/token and connection probe | `app/src/main/kotlin/com/sdelcore/hermex/MainActivity.kt` |
| Onboarding | `HermesMobile/Features/Onboarding/` | partial — connection form; Connect and Test Connection open an already-saved Hermes server at its address without probing/signing in | `app/src/main/kotlin/com/sdelcore/hermex/MainActivity.kt` |
| Share | `HermesShareExtension/` | n/a — Android shares through intents, no iOS extension | — |
| MoveToHermes | `HermesMobile/Features/MoveToHermes/` | todo | — |
| Workspace | `HermesMobile/Features/Workspace/` | todo | — |
| Push notifications | `HermesMobile/Push/` | todo | — |
| Live activities | `HermesMobile/LiveActivities/`, `HermesLiveActivityWidget/` | n/a — iOS ActivityKit/WidgetKit surfaces | — |
| App Intents | `HermesMobile/AppIntents/` | n/a — iOS App Intents API | — |

## Upstream sync

- Synced through `c08c398db6511e8f3b99bd48e0c9d976b153221e`.
- `c6cb3a9` (currency amounts pairing into inline math) has no Android markdown/math renderer to affect.
- `da69ea4` (session edits on separate-gateway hosts) has no corresponding Android session-edit operations.
- `c08c398` (follow a host-replaced Bot Chat) cannot be applied to the current Android app: it has no Bots surface or Hermes gateway conversation identity/reattach flow. The existing generic sessions/chat flow addresses sessions by fixed session ID and has no canonical Bot Chat lookup or Bot Chat cache. Adding those capabilities would be a new feature, not a faithful port of this fix.
