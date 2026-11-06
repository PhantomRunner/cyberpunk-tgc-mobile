# Cyberpunk TCG Companion

A small, offline-first Android college project for managing a physical card
collection and saved decks.

## Development build

The current development build includes the following screens:

- Home
- Collection
- My decks
- Scanner
- Settings

### Home

- Shortcuts open Collection and My decks while restoring the state of tabs.
- Shows the number of decks saved in the current session and the three most recently
  created decks: name, Draft status, Main and Legends counts, and previews of
  selected Legends. Tapping a deck opens its editor; View all decks opens the full
  list. Unsaved edits are not shown here.
- Total cards, Unique cards and Complete sets currently display dashes:
  collection statistics are not calculated yet.

### Collection

- Displays a grid of six fictional demo cards with placeholder images and borders
  matching each card's color.
- Supports name search, multiple type and color selections with independent filter resets.
- Cards can be sorted by Name, Cost, Power, RAM, Color and Type.
- Tapping a card opens Card Details: name, subtitle, type, color, Cost, Power, RAM,
  unit types and description. Search conditions and scroll position kept intact and recovered
  when returning to the list.
- Card view is read-only. Owned quantities and Room storage for the collection not 
implemented yet.

### My decks

- Creating a deck with a nonblank name, listing decks with Draft status
  and separate Main/Legends counts, opening the editor, and deleting with confirmation 
  are implemented features.
- Deck Editor allows renaming a deck and selecting or removing up to three Legends
  with distinct names with current demo catalog containment only one Legend.
- Add cards opens a Unit, Program and Gear catalog with search, filters and sorting.
  The +/− buttons change quantities; reducing a quantity to zero removes the card.
  Cards sharing the same name and subtitle are limited to three copies in total. (Legal game rule)
  Adding a card requires a selected Legend of the same color.
- Shows the deck composition, RAM limits for all four colors, and warnings about
  the Legend count, Main size (40–50), colors and RAM limits overflow.
  Incomplete decks can be saved as Drafts.
- Save draft saves the working copy within the current session. Using Back with
  unsaved edits offers a choice to discard changes or keep editing.
- Deck Preview shows the current working copy, with Legends separate from Main,
  copy counts and access to full card details. Previewing does not save changes.
- Decks and edits survive screen rotation and tab switching, but are reset when
  the app process ends. Persistent storage, deck list sharing and shake-to-shuffle
  are not implemented yet.

### Scanner

- Uses CameraX: opening the tab requests CAMERA permission and starts a rear-camera
  preview. Handles permission denial, opening app settings, missing cameras and retrying.
- Take photo captures one image as a temporary JPEG in the app's private cache.
  Repeated capture requests are blocked while a photo is being taken.
- After capture, the full image is displayed with its EXIF orientation applied.
  Retake deletes the previous photo and reopens the camera. Capture and image-reading
  errors are shown on screen.
- A completed photo survives rotation and moving the app to the background.
  Leaving Scanner through another tab or Back resets the photo. The camera is
  released during photo review and when leaving the screen; lifecycle handling
  stops it in the background. Files left after the process ends are cleaned up
  the next time Scanner opens.
- OCR, card matching, manual confirmation and adding cards to the collection are
  not implemented yet. Scanner runs inside MainActivity; a separate ScannerActivity
  and an explicit Intent to open it have not been added yet.

### Settings

- Supports manual Light/Dark switching across the app, including the system bars.
- The selection is stored locally in SharedPreferences and restored after restart.
  Dark is the default.
- Auto mode and ambient light sensor integration are not implemented yet.

## Architecture Perspective

### Phase 1

The current implementation uses one Android module, `app`. Its only activity,
`MainActivity`, is the entry point. It applies the Material 3 theme and hosts
`CompanionApp`, which contains the top and bottom bars. `AppNavHost` defines the
Compose Navigation routes. Card and deck routes carry the selected record's ID.

The app follows a simple MVVM structure. Compose screens display state from
ViewModels and pass user actions back to them. When the state changes, Compose
updates the parts of the screen that use it.

- Collection and Add cards browse the `DemoCards` catalog using the same
  `CardBrowserViewModel` logic. Each keeps its own search, filters and sort order.
- `DecksViewModel` is shared by Home and the deck screens. The editor and Preview
  show the working copy while you edit. Save draft updates the version shown on
  Home and My decks. Each `Deck` stores Legend IDs and `DeckItem` records with
  card IDs and copy counts. Kotlin functions in `domain` calculate RAM limits
  and deck warnings.
- Scanner is a tab in the same activity. CameraX handles the camera preview and
  takes one photo at a time. `ScannerViewModel` tracks the capture and the photo
  being reviewed.

#### State and storage

- **Decks** stay in memory for the current session, including saved drafts and
  unsaved edits. They survive tab switches and screen rotation. The deck list
  starts empty when the app process restarts.
- **Browsing state** uses Android's saved UI state. `SavedStateHandle` keeps the
  search, filters and sort order. Compose Navigation saves the back stack and
  Compose saves the grid position. Android can restore these when it recreates
  the same task after the app process ends.
- **Scanner photos** are temporary JPEGs in the app's private cache. A captured
  photo stays in review through rotation or a return from the background while
  the process is alive. Retake or leaving Scanner discards the photo. After a
  process restart, Scanner starts a new capture session and clears leftover files.
- **Between launches**, `ThemePreferences` restores the Light/Dark choice from
  `SharedPreferences`. The same six demo cards load from code on every launch.
  Room storage for the collection and decks is planned for Milestone 2.
