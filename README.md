# Cyberpunk TCG Companion

A small, offline-first Android college project for managing a physical card
collection and saved decks. The current app has a navigation shell with blank
screens for Home, Collection, Card Details, My Decks, Deck Editor, Deck Preview,
Scanner and Settings. Screen content, collection/deck data, photography, OCR
and sensors are planned in later tasks.

## Reading the code

Start at `MainActivity.kt`, which applies the theme and displays `CompanionApp`.
In the `navigation` package:

- `AppDestination` names each route and its parent tab.
- `CompanionApp` owns the navigation controller and handles tab switching.
- `AppTopBar` and `AppBottomBar` in `AppBars.kt` render values and report button taps.
- `AppNavHost` connects routes to screens and supplies their navigation callbacks.

Names ending in `ResId` refer to Android resources; `stringResource` and
`painterResource` load the corresponding text and images.

A function marked `@Composable` describes UI that Compose can update when its
observed state changes. A callback such as `onOpenCard: () -> Unit` is a function
the screen calls when the user taps a button. This keeps screens focused on
rendering and user actions.
`currentBackStackEntryAsState()` lets Compose update the bars when the route
changes. The controller saves a tab's stack when leaving and restores it on
return, so a detail screen stays open when switching tabs.

`ScreenPlaceholder` shares the temporary layout; its optional `content` function
lets a screen add navigation buttons. The `ui/theme` package defines Material 3
colors and typography. No ViewModel is needed yet because these placeholders
have no data or business state; navigation state belongs to the controller.
