# Atomic Task Backlog

**Instructions:** Pick one unchecked item only; respect dependencies. After acceptance criteria pass, mark exactly that item `[x]`, commit it with task changes in **one commit**, then stop. Commit subjects use exactly `(type) description`. Do not mark unverified work complete.

## Milestone 1 — UI & Navigation (~25%)

- [x] **M1-01** — Initialize a minimal Android Kotlin + Compose application with Gradle wrapper, `.gitignore`, manifest and README. **Accept:** Gradle project imports and `assembleDebug` succeeds when SDK is present. **Commit:** `(build) initialize android compose project`
- [ ] **M1-02** — Establish a restrained cyberpunk Material 3 theme with light/dark palettes and typography. **Depends:** M1-01. **Accept:** app displays themed content in both modes. **Commit:** `(style) add application theme`
- [ ] **M1-03** — Add Compose Navigation plus destination definitions and the bottom/main navigation shell. **Depends:** M1-02. **Accept:** navigation among empty placeholder destinations works and Back behaves normally. **Commit:** `(feat) add navigation shell`
- [ ] **M1-04** — Add synthetic card/deck models and in-memory demo records (including image placeholders). **Depends:** M1-03. **Accept:** reusable demo content renders without external assets. **Commit:** `(chore) add sample card and deck data`
- [ ] **M1-05** — Build reusable card tile, stat chip, empty state and deck row components. **Depends:** M1-04. **Accept:** components preview/render with demo data. **Commit:** `(feat) add reusable collection components`
- [ ] **M1-06** — Implement Home screen with collection summary, saved-deck preview and navigation shortcuts. **Depends:** M1-05. **Accept:** screen renders demo stats; shortcuts navigate. **Commit:** `(feat) build home screen`
- [ ] **M1-07** — Implement Collection grid with UI search and type filters on in-memory data. **Depends:** M1-05. **Accept:** query/filter visibly alter displayed mock cards. **Commit:** `(feat) build collection screen`
- [ ] **M1-08** — Implement Card Details layout with placeholder art, metadata, quantity control presentation and add-to-deck action presentation. **Depends:** M1-07. **Accept:** tapping a mock card opens the correct detail; unimplemented actions are explicitly demo-only/disabled. **Commit:** `(feat) build card details screen`
- [ ] **M1-09** — Implement My Decks list and create/rename/delete dialogs with mock state only. **Depends:** M1-06. **Accept:** demo decks shown and dialog flows work locally. **Commit:** `(feat) build saved decks screen`
- [ ] **M1-10** — Implement Deck Editor and Preview mock layouts with selected cards, total count, pick-card UI and shuffle affordance. **Depends:** M1-09. **Accept:** opened from My Decks; local UI interactions show planned flow, without persistence. **Commit:** `(feat) build deck editor screens`
- [ ] **M1-11** — Implement Scanner mock layout: camera placeholder, capture/preview/result states and manual-search fallback presentation. **Depends:** M1-03. **Accept:** states can be previewed without live camera/OCR. **Commit:** `(feat) build scanner prototype screen`
- [ ] **M1-12** — Implement Settings mock layout with Light/Dark/Auto selector and app information (Auto can be demo-only). **Depends:** M1-02. **Accept:** selector updates local UI theme or clearly labeled demo state. **Commit:** `(feat) build settings screen`
- [ ] **M1-13** — UI-only acceptance pass: fix navigation, placeholder states, narrow layout problems. **Depends:** M1-08 through M1-12. **Accept:** app builds, every screen is reachable and visible actions are either demo-functional or explicitly disabled. **Commit:** `(fix) complete ui milestone navigation review`

## Milestone 2 — Core Functionality (~55%)

- [ ] **M2-01** — Add Room build configuration and base database class. **Depends:** M1-13. **Accept:** project builds and Room instance can be created. **Commit:** `(build) configure room persistence`
- [ ] **M2-02** — Implement Card and CollectionEntry entities with constraints and DAO operations. **Depends:** M2-01. **Accept:** insert/update/read/delete covered by targeted database tests where runnable. **Commit:** `(feat) add card collection persistence`
- [ ] **M2-03** — Implement Deck and DeckCard entities, relationships and DAOs. **Depends:** M2-02. **Accept:** deck membership and cascade/foreign-key behavior are verified. **Commit:** `(feat) add deck persistence`
- [ ] **M2-04** — Implement repository layer and seed a small synthetic card catalogue on first run. **Depends:** M2-03. **Accept:** repeat startups do not duplicate seeds; repository reads return the initial catalogue. **Commit:** `(feat) add repository and sample catalogue`
- [ ] **M2-05** — Connect Collection list/search/filter to real Room-backed ViewModel state. **Depends:** M2-04. **Accept:** visible catalogue loads from Room and filters work. **Commit:** `(feat) connect collection to database`
- [ ] **M2-06** — Implement collection quantity create/update/delete from Card Details. **Depends:** M2-05. **Accept:** quantities survive restart; zero owned copies handled consistently. **Commit:** `(feat) manage owned card quantities`
- [ ] **M2-07** — Replace Home mock collection statistics with Room-derived totals. **Depends:** M2-06. **Accept:** totals update after inventory changes. **Commit:** `(feat) show live collection statistics`
- [ ] **M2-08** — Connect My Decks list, create, rename and delete to Room. **Depends:** M2-04. **Accept:** saved decks survive restart; deletion requires confirmation. **Commit:** `(feat) persist saved decks`
- [ ] **M2-09** — Connect Deck Editor card membership and quantities to Room. **Depends:** M2-08, M2-06. **Accept:** updates persist; missing owned copies do not silently erase deck contents. **Commit:** `(feat) persist deck card selections`
- [ ] **M2-10** — Implement deck size and owned-versus-required indicators with straightforward validations. **Depends:** M2-09. **Accept:** counters and availability update when collection/deck changes. **Commit:** `(feat) add deck quantity validation`
- [ ] **M2-11** — Implement deck preview and pure in-memory shuffle operation. **Depends:** M2-09. **Accept:** shuffle changes only preview order, never saved deck contents. **Commit:** `(feat) add deck preview shuffle`
- [ ] **M2-12** — Implement a dedicated Compose-hosted ScannerActivity and explicit Intent from MainActivity. **Depends:** M1-11. **Accept:** tap scanner shortcut launches ScannerActivity through a real explicit Intent and returns safely. **Commit:** `(feat) launch scanner with explicit intent`
- [ ] **M2-13** — Implement single-photo capture via Android camera contract and safe URI handling. **Depends:** M2-12. **Accept:** photo capture/retake works; cancellation is graceful; no video/continuous scanning. **Commit:** `(feat) capture card photographs`
- [ ] **M2-14** — Add captured-image review screen and image loading/error state. **Depends:** M2-13. **Accept:** user sees captured photo and can retry or proceed. **Commit:** `(feat) review captured card images`
- [ ] **M2-15** — Integrate ML Kit OCR for a captured still image. **Depends:** M2-14. **Accept:** recognized text or actionable failure displayed; never claims guaranteed identification. **Commit:** `(feat) recognize text from card photos`
- [ ] **M2-16** — Match OCR text against the local catalogue and display ranked candidates. **Depends:** M2-15, M2-05. **Accept:** known sample title returns candidates; uncertain matches clearly identified. **Commit:** `(feat) suggest matching cards from ocr`
- [ ] **M2-17** — Implement explicit confirmation to add recognized card to collection; prevent double-submit. **Depends:** M2-16, M2-06. **Accept:** quantity increases once per confirmation only. **Commit:** `(feat) confirm scanned cards in collection`
- [ ] **M2-18** — Implement manual card-name search and selection fallback in Scanner. **Depends:** M2-17. **Accept:** users can add a card if OCR fails or the camera is unavailable. **Commit:** `(feat) add manual scanner fallback`
- [ ] **M2-19** — Implement theme preference Light / Dark / Auto in persistent app settings. **Depends:** M1-12. **Accept:** preferences survive restart and manual themes work without a sensor. **Commit:** `(feat) persist theme preferences`
- [ ] **M2-20** — Integrate ambient light sensor for Auto theme with debounce/hysteresis and absent-sensor fallback. **Depends:** M2-19. **Accept:** Auto changes theme when environmental light crosses stable thresholds; manual themes unaffected. **Commit:** `(feat) add ambient light auto theme`
- [ ] **M2-21** — Add low-light advisory in Scanner using light sensor, with graceful fallback. **Depends:** M2-20, M2-14. **Accept:** warning appears only in low-light Scanner context and does not block photo capture. **Commit:** `(feat) warn about low light while scanning`
- [ ] **M2-22** — Implement lifecycle-aware shake detection with adjustable threshold and cooldown. **Depends:** M1-03. **Accept:** an intentional shake triggers once and normal movement does not easily retrigger. **Commit:** `(feat) detect accelerometer shake gesture`
- [ ] **M2-23** — Connect shake gesture to shuffle on Deck Preview. **Depends:** M2-22, M2-11. **Accept:** shake randomizes preview only; saved order/data unchanged. **Commit:** `(feat) shake to shuffle deck preview`
- [ ] **M2-24** — Connect shake gesture to selecting a random saved deck on Home/My Decks. **Depends:** M2-22, M2-08. **Accept:** when at least one deck exists, shake selects/displays a saved deck; empty state is safe. **Commit:** `(feat) shake to select random deck`
- [ ] **M2-25** — Implement share deck list using implicit ACTION_SEND chooser and readable text. **Depends:** M2-09. **Accept:** compatible external sharing apps can receive deck text. **Commit:** `(feat) share deck list with android intent`
- [ ] **M2-26** — Remove mock-only actions and complete MVP integration audit. **Depends:** M2-05 through M2-25. **Accept:** every control either works with real data or is visibly unavailable; run available build/tests; document hardware-only checks. **Commit:** `(fix) complete core functionality integration`

## Milestone 3 — Polish, Testing & Release (~20%)

- [ ] **M3-01** — Refine loading, empty and failure states across primary screens. **Depends:** M2-26. **Accept:** no blank unexplained states in typical flows. **Commit:** `(fix) improve ui state feedback`
- [ ] **M3-02** — Add safe confirmations and undo/clear feedback for destructive actions. **Depends:** M3-01. **Accept:** delete operations cannot be accidentally triggered by a single ambiguous tap. **Commit:** `(feat) improve destructive action confirmations`
- [ ] **M3-03** — Improve permissions, cancellation and unavailable-hardware handling. **Depends:** M2-26. **Accept:** camera cancellation, absent sensors and denied access don't crash the app. **Commit:** `(fix) handle missing device capabilities`
- [ ] **M3-04** — Add tests for card collection/deck operations and integrity rules. **Depends:** M2-26. **Accept:** targeted tests pass when environment supports them. **Commit:** `(test) cover collection and deck logic`
- [ ] **M3-05** — Add tests for OCR matching, shake cooldown and automatic theme thresholds. **Depends:** M2-26. **Accept:** relevant deterministic pure logic has automated tests. **Commit:** `(test) cover recognition and sensor rules`
- [ ] **M3-06** — Improve accessibility labels, contrast, responsive spacing and font scaling. **Depends:** M3-01. **Accept:** main screens remain usable at larger text sizes; actionable controls are labeled. **Commit:** `(style) polish accessibility and layouts`
- [ ] **M3-07** — Review performance and lifecycle handling. **Depends:** M3-03. **Accept:** no obvious camera/observer leaks or main-thread database operations; smoke test navigation. **Commit:** `(refactor) improve lifecycle and performance`
- [ ] **M3-08** — Document local build, device requirements, app features and demo path in README. **Depends:** M3-04 through M3-07. **Accept:** fresh contributor can follow README; sensor/camera availability documented. **Commit:** `(docs) add build and demo instructions`
- [ ] **M3-09** — Perform final hands-on check on Android device or emulator and record a demonstration checklist; fix any critical blocker found before completion. **Depends:** M3-08. **Accept:** test checklist records outcomes, limitations and fixes where needed. **Commit:** `(test) document final demonstration checks`
- [ ] **M3-10** — Build and verify deliverable APK; document output path and submission checklist. **Depends:** M3-09. **Accept:** build command succeeds and APK exists where documented (or limitation clearly recorded without marking complete). **Commit:** `(build) prepare final android release`

## Course evidence checklist (checked during M2/M3)

- [ ] At least three distinct functional screens.
- [ ] Full persistent CRUD operations.
- [ ] Camera photo capture plus offline/single-image recognition and manual fallback.
- [ ] Ambient light sensor used for theme and low-light warning.
- [ ] Accelerometer used for shake controls.
- [ ] Actual explicit Intent to another app Activity.
- [ ] Actual implicit Intent for Android sharing.
- [ ] Works without a hosted backend.
- [ ] App builds and demo flow is reproducible.
