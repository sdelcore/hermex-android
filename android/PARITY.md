# Hermex Android parity roadmap

Statuses: `done` means this Android slice is implemented; `partial` means only the listed first-slice path exists; `todo` means not ported; `n/a` means the iOS-only capability does not have an Android equivalent in scope.

| Feature area | Upstream Swift path | Android status | Kotlin owner |
|---|---|---|---|
| Chat | `HermesMobile/Features/Chat/` | partial — send, token stream, stop, dismissable errors | `app/src/main/kotlin/com/sdelcore/hermex/MainActivity.kt` |
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

### Synced through f24d333

Recorded sync point: `f24d333b98915988da3e1b0eccf54c5f009e3c15` (previous: `c08c398db6511e8f3b99bd48e0c9d976b153221e`).

199 commits reviewed. One had a surface in this port and was ported; the rest have no Android equivalent.

- `d96fb82` fix(hermes): explain that a gateway's own listener isn't supported yet — not applicable: the Android connect form only probes `/health`; it has no gateway-listener detection or staged connect errors to explain.
- `c21e2aa` fix(tasks): show a script-only run's output from its row — not applicable: no Tasks surface.
- `5125b51` fix(chat): keep Listen's controls when the phone's voice reads a reply — not applicable: no Listen/TTS playback.
- `a638155` fix(chat): stop a long inline-heavy paragraph from crashing the app on open — not applicable: replies are plain `Text`; there is no markdown or math renderer to crash.
- `bd877f8` chore(scripts): shut down a simulator test-sim booted once its run ends — not applicable: iOS simulator script; Android builds with Gradle.
- `153f755` fix(voice): record Server First dictation as 24 kHz / 48 kbps AAC — not applicable: no dictation.
- `faec02c` feat(chat): explain approval commands in plain words — not applicable: the port has no approval cards or approval commands.
- `b9b426b` fix(hermes): balance the Bots and Sessions switch — not applicable: no Bots inbox or home switch.
- `d420168` fix(chat): queue a file-only draft mid-run instead of offering Stop — not applicable: no attachments and no draft queue.
- `3d8be3a` fix(chat): keep the composer editable while the chat is offline — not applicable: the Android composer is always editable; there is no offline chat state to gate it.
- `033cdf0` feat(chat): make hardware-keyboard sending truthful and complete — not applicable: the composer has no keyboard-action handling or send-gate states.
- `9905e06` perf(chat): load transcript media only once it scrolls into view — not applicable: no transcript media.
- `988063c` perf(bots): show the cached roster while the inbox connects — not applicable: no Bots roster.
- `217beb6` test: share observation waits and flag sleep-based test ordering in CI — not applicable: iOS XCTest helpers; the Android suite is plain JUnit with no such CI lint.
- `e26e456` fix(hermes): probe the gateway socket when the network changes — not applicable: no gateway socket or connectivity-change probing.
- `a1db996` fix(sessions): restore iPad keyboard commands on the Hermes home — not applicable: iPadOS keyboard commands.
- `6c9156d` fix(hermes): let the composer's `@` panel find files in any folder — not applicable: no `@` file panel.
- `84d8386` feat(sessions): archive a session with a full swipe — not applicable: the Android session list is read-only; there is no archive mutation.
- `0eaf060` fix(live-activity): declare frequent updates for push-driven Live Activities — not applicable: ActivityKit.
- `15f89b6` test: trim redundant tests from the #1360 epic PRs — not applicable: iOS tests.
- `abe9f6f` fix(chat): delay and hold the stream recovery chip so quick recoveries don't flash — not applicable: no reconnect chip; the Android chat has no recovery UI.
- `6e4e7b3` fix(home): say "Reconnecting" on populated Sessions and Bots lists instead of going quiet — not applicable: the Android list has no reconnect state.
- `be9712e` feat(sessions): lift sessions waiting for you above the rest of the list — not applicable: rows decode only id and title; there is no waiting state.
- `31593d7` fix(chat): make rendered replies and code follow Dynamic Type — not applicable: no rendered markdown/code view; text uses the Material type scale.
- `f1eae3e` perf(chat): stop rebuilding settled reply selection hosts when nothing changed — not applicable: no text-selection host in the port.
- `a8cb5b6` fix(networking): stop telling Hermes users to check hermes-webui — not applicable: the port surfaces a raw HTTP code or IO message; the hermes-webui guidance copy does not exist here.
- `93e77b8` feat(chat): name what a tool group did instead of counting hidden calls — not applicable: no tool-call rendering.
- `2bcf2b2` feat(chat): show the latest thought while thinking, and Thought once settled — not applicable: no reasoning display.
- `557be4a` feat(chat): keep answered questions readable in the transcript — not applicable: no question/answer cards.
- `6df56f8` feat(chat): show the queued prompt as a pending bubble — not applicable: no prompt queue.
- `14d8258` feat(chat): turn large pasted text into a removable attachment — not applicable: no attachment model.
- `fc7bb33` chore(release): add build 3's changes to the Hermex 2.0 changelog — not applicable: `CHANGELOG.md` is the iOS release record; this port tracks parity here instead.
- `8f20bfd` chore(release): close out Hermex 2.0 — not applicable: release bookkeeping (changelog and Xcode version).
- `1cfb70b` chore(scripts): let test-sim builds take turns on 16 GB Macs — not applicable: iOS simulator script.
- `7ff9c8c` feat(workspace): start lockfiles and huge diffs collapsed — not applicable: no diff viewer.
- `8f1dfe4` feat(workspace): remember viewed and collapsed files while a chat is open — not applicable: no diff viewer.
- `f241c79` feat(hermes): review a branch's committed changes beside uncommitted ones — not applicable: no Git review surface.
- `b0d7a65` fix(hermes): keep a turn's committed work in its turn-changes card — not applicable: no turn-changes card.
- `27aa270` feat(workspace): color diff syntax and wrap diff lines — not applicable: no diff viewer.
- `f80ac37` feat(workspace): jump to any changed file from inside the diff — not applicable: no diff viewer.
- `229d07c` feat(workspace): comment on selected lines and batch several into one prompt — not applicable: no diff viewer.
- `3ac710c` feat(hermes): search Files across the workspace, not just opened folders — not applicable: no Files browser.
- `4119d61` feat(workspace): show a Markdown file's source, and copy any file's path — not applicable: no Files browser.
- `71f4bc0` feat(hermes): show the branch's pull request in the Git menu — not applicable: no Git menu.
- `5dc5e2f` chore(triage): let a Hermes agent triage Hermex problems and draft the issue — not applicable: `.github/triage` workflow for upstream issues.
- `ffac45b` docs: refresh the README for Hermex 2.0 — not applicable: upstream README and docs assets.
- `13fb048` fix(live-activity): quiet working states and remove invented progress — not applicable: ActivityKit widget.
- `dcaf64e` fix(live-activity): adapt colors to the host and system material — not applicable: ActivityKit widget.
- `469a1df` feat(live-activity): add a small Watch and CarPlay layout — not applicable: ActivityKit widget.
- `9f97410` feat(settings): copy support details and prefill Report a Problem — not applicable: no About or Report a Problem surface.
- `04a2ef6` feat(settings): keep local crash and hang reports with MetricKit — not applicable: MetricKit is iOS-only.
- `c93f28c` feat(intents): prefill a new chat with content from Shortcuts — not applicable: App Intents.
- `e617d04` feat(hermes): show the stages reached by a failed connection attempt — not applicable: the port has a single `/health` probe, not a staged connect flow.
- `f91b6b7` fix(share): explain which shared items were left out — not applicable: no share extension or import staging.
- `6846553` fix(insights): keep last-known limit cards when a refresh fails — not applicable: no Insights surface.
- `f35a52c` feat(settings): show the bundled open-source notices under About — not applicable: no About surface.
- `f8fa598` feat(settings): troubleshoot a problem with your Hermes agent from About — not applicable: no About surface.
- `f2fbc6d` feat(chat): send a message typed offline once the chat reconnects — not applicable: no offline message queue or reconnect loop.
- `5b2255e` feat(widgets): show provider limits on the Home Screen and Lock Screen — not applicable: WidgetKit.
- `6f78f86` refactor(ui): one toast with tone, busy-to-result updates, and swipe to dismiss — not applicable: the port has no shared toast component; screens show inline text.
- `df6bca1` feat(chat): swipe between a message's images in the lightbox — not applicable: no images.
- `e9eeb97` feat(chat): fold a scheduled task's host prompt into a "Scheduled task" card — not applicable: no scheduled-task cards.
- `6167cc9` fix(chat): clear the stuck "Connection lost" banner after reconnecting — not applicable: the chat has no connection banner.
- `00cb56c` chore(scripts): stop test-sim hangs and leftover simulators from stalling 16 GB lanes — not applicable: iOS simulator script.
- `9ab09d9` chore(ci): build PRs and release candidates with Xcode 27.1, the Xcode used locally — not applicable: iOS workflows; `android-ci.yml` already pins Temurin 21 with SDK 35.
- `4c66bdd` perf(chat): count transcript body evaluations during a scripted stream — not applicable: SwiftUI render budgeting.
- `e94ac12` docs(agents): write down the performance invariants and what guards each — not applicable: docs for the Swift app.
- `da70c38` fix(chat): keep the reader's place when the transcript rewraps — not applicable: no scroll-managed transcript.
- `7f874b6` docs(readme): update the supporters wall for October 2026 — not applicable: upstream README.
- `2ffc3dc` perf(chat): stop re-deriving the whole transcript on every streaming tick — not applicable: SwiftUI view-model perf.
- `733ae05` fix(hermes): keep a turn's cards in place after a mid-turn reconnect — not applicable: no turn cards and no reconnect.
- `cbc43bd` docs(dev): order a slice's test runs to need fewer builds — not applicable: iOS development docs.
- `8de33e3` chore(ci): free the runner at once when a newer push cancels PR CI — not applicable: iOS workflow.
- `28895f2` perf(chat): skip the math scan for prices and paths, and keep shell variables out of math — not applicable: no math renderer.
- `ee29c58` feat(hermes): let users choose browser sign-in on a password host — not applicable: the port connects with a token only; there is no browser sign-in flow.
- `fe4c5dd` feat(onboarding): one address-first Add Server sheet for first launch and Settings — not applicable: the port already has one address-first form (base URL and token) shared by first launch and Settings; nothing to consolidate.
- `6b81b51` fix(hermes): remove the gray bar from the iPad sidebar — not applicable: iPadOS sidebar chrome.
- `4272b5d` docs(agents): skip the local full suite when a project diff only removes sources — not applicable: agent docs.
- `ac28f61` fix(chat): keep MEDIA: tokens inside inline code literal — not applicable: no media token parser.
- `2f20d9b` fix(chat): deliver files wrapped in backticked MEDIA tags — not applicable: no media delivery.
- `a0267c9` fix(composer): insert dictation at the caret and never overwrite later typing — not applicable: no dictation.
- `066aa54` feat(composer): replace the scrolling toolbar with a fixed row and Model settings — not applicable: the composer is a single text field with no toolbar or model settings.
- `effd19f` feat(bots): add 21 avatars and let the host's model draw new ones — not applicable: no Bots.
- `0c922fb` fix(chat): show active subagents inline — not applicable: no subagent display.
- `661a9b6` feat(app): optimize Hermex for iPhone Duo — not applicable: iOS layout work.
- `c152914` fix(intents): keep server URLs out of the app group — not applicable: no App Intents or app group.
- `5459cb7` fix(chat): keep RTL replies with code on screen — not applicable: no code blocks; Compose text already lays RTL out.
- `4b2a3fe` fix(hermes): show the host's reason when it refuses a file read — not applicable: no file reads.
- `d40d802` fix(kanban): count a retried Card's age from its current run — not applicable: no Kanban.
- `dcbc034` fix(hermes): stop offering a downgrade as "Update available" — not applicable: no update or version UI.
- `e13f649` fix(hermes): keep Memory on the host when a Profile's terminal is SSH — not applicable: no Memory surface.
- `8857688` chore(hermes): advance the hermes-agent pin to 0.22.5 — not applicable: hermes-agent pin and fixture bookkeeping; the port keeps no Hermes fixtures.
- `135ad1f` fix(chat): keep your place when rotating, and stop the Plus/Max rotation lag — not applicable: iOS rotation and layout.
- `b22d788` feat(chat): view and edit a pasted-text attachment before sending — not applicable: no attachments.
- `4668e79` feat(bots): watch and control a host's screen from the phone — not applicable: no Screen surface or screen-session client.
- `8073ec9` test: stop the real on-device speech test from slowing PR CI — not applicable: iOS tests.
- `8f5fb09` test: keep the Screen tests off the real notification center — not applicable: iOS tests.
- `ed15e6e` fix(scripts): get local-hermes installing and managing plugins at the current pin — not applicable: macOS dev script.
- `690fb9e` feat(settings): manage a server's plugins and browse the marketplace — not applicable: no plugins surface.
- `9cbd343` docs(agents): correct when plugin routes need a host restart — not applicable: agent docs.
- `11cc1b9` feat(appearance): add app-wide text size and code display settings — not applicable: no appearance settings.
- `913b5c7` feat(appearance): add themes and an accent color — not applicable: a single `MaterialTheme`; there is no theme or accent system.
- `54d898b` feat(appearance): add an app-wide font setting — not applicable: no appearance settings.
- `fdae3bd` feat(chat): read the transcript while an approval is pending — not applicable: no approvals.
- `3930c1c` feat(settings): let Hermes servers hide home rows — not applicable: no home rows.
- `ec066d9` fix(home): put the home's switch, search, and avatar in the iPhone Duo rail — not applicable: iOS layout.
- `8dd1e4f` fix(home): move the unfolded iPhone Duo's controls into its rail — not applicable: iOS layout.
- `b82cd90` chore(hermes): advance the hermes-agent pin to 0.23.0 — not applicable: pin bookkeeping.
- `1e541e4` fix(scripts): let a backgrounded local-hermes stop on SIGINT — not applicable: macOS dev script.
- `7547796` fix(hermes): refuse a Webapp address at Add Server and offer a way out — not applicable: the port has no Webapp-versus-dashboard protocol detection.
- `fe501d0` feat(hermes): speak the canonical gateway session protocol — not applicable: the port speaks the webui `/api/chat/*` and `/api/sessions` contract only; it has no Hermes gateway conversation client to move onto the canonical protocol.
- `72395bf` feat(hermes): show why a Webapp serves only one Profile and fix it from the phone — not applicable: no Webapp or profile handling.
- `a70e76e` fix(chat): remove the dark fade behind the composer — not applicable: no glass or gradient composer.
- `b226d63` fix(ui): drop the divider lines between rows — not applicable: rows are `TextButton`s with no dividers.
- `23bbaea` fix(chat): keep the composer above the keyboard when / or @ opens on short screens — not applicable: no `/` or `@` panels.
- `cd16099` chore(ci): configure CodeRabbit for stacked PRs and drop its docstring check — not applicable: review-bot config for the Swift project.
- `458596b` chore(release): Hermex 2.1 changelog — not applicable: iOS release notes.
- `b15b4eb` fix(chat): close the gap above the header on the folded iPhone Duo — not applicable: iOS layout.
- `342b1ef` chore(release): close out Hermex 2.1 — not applicable: release bookkeeping.
- `9fb1917` fix: small bugs, dead code, and stale docs from a full-project audit — not applicable: its webui query fix (`+` must be sent as `%2B`) cannot bite here, because the Android port builds query values with `URLEncoder`, which already emits `%2B` for a literal plus.
- `b3395ac` perf(chat): stop redoing math scans, reasoning chunking, and image decoding on every update — not applicable: no markdown, reasoning, or image pipeline.
- `491f1e3` perf(sessions): read ordinary and scheduled session pages at the same time — not applicable: the port reads one `/api/sessions` page and has no scheduled page.
- `a1d4e5c` chore(hermes): advance the hermes-agent pin to 0.23.1 — not applicable: pin bookkeeping.
- `d76824e` feat(settings): connect apps like Notion and Slack to Hermes from the phone — not applicable: no integrations surface.
- `1ff2ea9` fix(sessionlist): close abandoned session-open signpost intervals — not applicable: `os_signpost` timing is iOS-only.
- `c6f3d2f` feat(bots): show a card in chat when the bot is using its screen — not applicable: no Screen surface.
- `542a812` fix(screen): stop retrying forever when the host's desktop can't capture — not applicable: no Screen surface.
- `9201332` chore(screen): pin hermex-screen to the stuck-driver fix — not applicable: no screen plugin pin.
- `7e5e8ab` feat(sessions): open the bot's Screen from a session chat — not applicable: no Screen surface.
- `dd18ec4` feat(sessions): a Screen button in the chat header — not applicable: the chat has no header actions or Screen surface.
- `bec80c6` feat(chat): open links like Gmail threads in their app, not the in-app browser — not applicable: replies are plain text and are not linkified.
- `5346b73` feat(chat): show agent HTML reports right in Hermes chats — not applicable: no HTML report rendering.
- `1a586d2` fix(chat): show Hermex photos as pictures in Hermes Desktop — not applicable: no image upload.
- `f4ae8c9` fix(app): support the Clear and Tinted home screen icons — not applicable: iOS asset catalog icons.
- `9afd216` feat(bots): open a bot's chat from a Home Screen widget — not applicable: no widgets.
- `9573196` feat(chat): view SVG code blocks as pictures — not applicable: no code block rendering.
- `c546e2f` fix(ui): stop screens jumping just after they open — not applicable: no async image measurement causing a first-frame jump.
- `7040042` feat(chat): view Mermaid code blocks as diagrams — not applicable: no code block rendering.
- `6267de2` feat(chat): optionally send with Return on a hardware keyboard — not applicable: the composer has no settings surface; adding the toggle would be a new feature rather than this fix.
- `5998b53` feat(sessions): ask for a GitHub star with a card in the session list — not applicable: the list has no card slots.
- `ff3f9ba` chore(dev): make the iPhone 17e the main checkout's simulator — not applicable: Xcode simulator preference.
- `bc1e202` feat(app): tally, preview and send the weekly usage report — not applicable: no usage surface.
- `08bc7a6` fix(settings): open the privacy policy from the Privacy Policy link — not applicable: no About or privacy link.
- `08c2d3e` docs(agents): retire the Hermes feature index — not applicable: agent docs.
- `af09b86` fix(chat): make the scroll-to-bottom arrow work after a send and mid-fling — not applicable: the transcript is a plain `Column`, not a scroll-managed list with an arrow.
- `7ec33d4` feat(settings): set a Profile's default model from a Hermes server's Settings — not applicable: no profile settings.
- `b076ab5` fix(sessions): start a new Hermes chat in the selected project's folder — not applicable: no projects or folders.
- `9610d08` feat(settings): Message Count, Workspace and Clear Offline Cache on a Hermes server's page — not applicable: no server page and no offline cache.
- `a6c2535` fix(chat): run /workspace and /interrupt in a Hermes chat instead of sending them as text — not applicable: the port has no slash-command routing; every message is sent as text.
- `3a0fbc9` feat(hermes): small parity polish for the session list, chat and Shortcuts — not applicable: Hermes-protocol session rows and Shortcuts specifics.
- `ac34424` feat(kanban): Only Mine, bulk Scheduled and Review, and Complete outside Review on Hermes — not applicable: no Kanban.
- `a91b03b` feat(sessions): show where a Hermes session came from and hide CLI and messaging sessions — not applicable: the row model here decodes only `session_id` and `title`; there is no origin field.
- `04c2c90` feat(chat): attach any file type in a Hermes chat — not applicable: no attachments.
- `9e29e6c` feat(usage): a 24-hour window in Usage on Hermes — not applicable: no usage surface.
- `5f07e0a` feat(sessions): copy a deeplink to a Hermes session — not applicable: the port registers no deep links.
- `ec08613` fix(chat): let the user dismiss a composer error, and clear it on the next edit — **ported**: `ComposerError` in `MainActivity.kt` clears a shown send/stream error when the draft is edited or `Dismiss` is tapped, with `ComposerErrorTest`.
- `34a4df0` feat(i18n): add Persian (fa) localization — not applicable: the port has no localization resources; its UI strings are hardcoded English.
- `18529ca` fix(chat): open the share sheet from the image lightbox — not applicable: no images.
- `8624f8f` feat(models): show readable model names in place of raw ids — not applicable: no model picker.
- `1bc3ed3` fix(chat): give the session titler the start of a long paste — not applicable: no session titler.
- `7e57571` fix(onboarding): stop Add Server crashing on a Cloudflare Access header name — not applicable: the connect form has no custom-header fields.
- `e6b80e3` HTML reports: --muted and --primary-foreground theme variables — not applicable: no HTML reports.
- `6c96d1d` fix(chat): keep a reply visible when a delegation delivery follows it — not applicable: no delegation deliveries.
- `8638ab9` fix(chat): keep a separate unsent New Session draft per project folder — not applicable: no draft store and no projects.
- `9b55fa3` chore(release): Hermex 2.2 changelog — not applicable: iOS release notes.
- `255a3c0` fix(tests): stop the usage report test failing now that its fixed week has passed — not applicable: iOS tests.
- `bbaeef1` feat(workspace): show HTML files rendered, and open linked reports instead of source — not applicable: no Files browser.
- `e02a9ff` fix(bots): clear a deleted archived Bot Chat from the offline cache — not applicable: no Bots and no offline cache.
- `db806df` fix(bots): forget a Profile's cached Bot Chat when the roster no longer lists it — not applicable: no Bots and no offline cache.
- `52d7a59` fix(bots): save and share a zero-byte MEDIA file — not applicable: no media download or share.
- `5958fc9` fix(sessions): keep the session list when a refresh lands while it connects — not applicable: the Android list loads once from `/api/sessions` and has no concurrent refresh.
- `62aee35` fix(chat): drop the empty trailing button when a chat has no actions — not applicable: the chat has no action row.
- `baae0dd` docs(agents): correct the describe RPC and plugin RPC notes for a gateway's Webapp — not applicable: agent docs.
- `ba40fcb` feat(app): report whether each Hermes server is a Webapp or a dashboard — not applicable: no protocol detection.
- `c8efb4d` fix(chat): answer the host's theme requests instead of stalling the turn — not applicable: the port answers no server requests, and the `set_theme` tool only reaches a Hermes gateway Webapp, which this port does not speak.
- `b1da6b4` fix(chat): route /rollback to the rollback calls so a Webapp host stops refusing it — not applicable: no slash-command routing.
- `8091a5c` feat(chat): show "Compacting context" while the host compacts — not applicable: the port shows no status line during a turn, so there is nothing to label.
- `b55d8a2` chore(scripts): stop test-sim passing when an --only selection ran no tests — not applicable: iOS script.
- `2a510fe` chore(release): close out Hermex 2.2 — not applicable: release bookkeeping.
- `175e23e` feat(chat): a View button on HTML code blocks — not applicable: no code block rendering.
- `c2851d6` feat(sessions): show a chat as working while its subagents or processes still run — not applicable: the Android list has no working state.
- `0805880` feat(sessions): filter the list by Working, Unread and Draft, and Mark All as Read — not applicable: no filters, and no unread or draft data in the row model.
- `3526b8f` feat(chat): show the host's notices — not applicable: the port renders no notices.
- `f6b3bd3` docs(triage): comment on a matching issue instead of filing a duplicate — not applicable: triage docs.
- `54cee68` feat(chat): remember Thinking and Fast for each model — not applicable: no model picker or Thinking/Fast controls.
- `dae93c2` test(sessions): stop the lost-socket reconnect test from racing its own tap — not applicable: iOS tests.
- `ff1f26f` test(chat): stop the width-crossing follower test from running out its wait — not applicable: iOS tests.
- `b9db7c4` feat(chat): set a model's remembered Thinking and Fast when a deferred pick lands — not applicable: no model picker.
- `e306196` test: stop the CI flakes at their source and cut the tests that kept causing them — not applicable: iOS tests.
- `c0d6c03` fix(workspace): preserve exact filenames in Files — not applicable: no Files browser.
- `f34839f` fix(tasks): show the reported outcome of recent runs — not applicable: no Tasks surface.
- `13b7b6f` chore: delete every Swift comment and keep the decisions in docs/adr — not applicable: it rewrites Swift sources and `docs/adr`; the Android sources keep their own comments.
- `ea49f89` chore(chat): remove the dead slash-command hold — not applicable: no slash commands.
- `c841485` refactor: one cancellation check for every screen — not applicable: the port has no per-screen cancellation layer; its single OkHttp callback path is never cancelled.
- `5667e37` fix(networking): never carry a webui request's custom headers to another origin — not applicable: the port sends no custom headers.
- `ceab4f1` test: wait on signals, not timeouts, in the chip-pass and workspace-registry tests — not applicable: iOS tests.
- `d35a0fc` fix(auth): keep legacy custom headers when their Keychain migration fails — not applicable: no custom headers, and Keychain is iOS-only.
- `a9861b9` test(live-activity): stop a leaked title refresh from failing the excerpt test — not applicable: ActivityKit tests.
- `f24d333` fix(chat): let a model pick right after a Thinking change go through — not applicable: no model picker.

### Synced through c08c398

- `c6cb3a9` (currency amounts pairing into inline math) has no Android markdown/math renderer to affect.
- `da69ea4` (session edits on separate-gateway hosts) has no corresponding Android session-edit operations.
- `c08c398` (follow a host-replaced Bot Chat) cannot be applied to the current Android app: it has no Bots surface or Hermes gateway conversation identity/reattach flow. The existing generic sessions/chat flow addresses sessions by fixed session ID and has no canonical Bot Chat lookup or Bot Chat cache. Adding those capabilities would be a new feature, not a faithful port of this fix.
