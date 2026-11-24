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
## Upstream sync: `1a12ab2b90d7459c96c45598936b671af412c04f`

Reviewed six Swift commits from `6508d74c` through `1a12ab2b`. None has an existing Android behavior to port: Android currently uses the legacy HTTP chat/session API and has no Hermes RPC turn recovery, attachment upload, move-to-Hermes prompt, scheduled-session view, or response-completion notification settings. Keep these items unapplied until their corresponding Android surfaces exist.

| Upstream commit | Android disposition |
|---|---|
| `0a637da` — Hermex 2.0 release changelog | iOS release-only changelog; no Android behavior. |
| `2f7e4bf` — ignore unseen Hermes turn failures for Retry | Not applicable: Android chat has no Hermes RPC turn coordinator or Retry action. |
| `5a11db1` — Windows attachment paths | Not applicable: Android chat has no attachment staging/upload or attachment-path parsing. |
| `47809f2` — wait for topmost sheet before Move question | Not applicable: Android has no MoveToHermes prompt or corresponding stacked-sheet workflow. |
| `3c38db3` — reopen View all sessions on foreground | Not applicable: Android has no scheduled/View all session list or Hermes session socket lifecycle. |
| `1a12ab2` — Open Settings for denied response alerts | Not applicable: Android has no response-completion notification setting or permission state. |
