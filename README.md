# SocialHub

SocialHub is a Compose-first Android app that models a lightweight social network: a feed (Hub), user profiles, search, and basic auth. It combines a local Room database, DataStore-backed session state, and Retrofit-backed remote refreshes for a fast, offline-friendly experience.

## Highlights

- Modern Android stack: Kotlin + Jetpack Compose + Navigation
- Offline-first reads: Room provides cached timelines, profiles, and searches
- Remote refreshes: Retrofit pulls data from DummyJSON and persists it locally
- Session-aware UX: Guest vs signed-in flows drive editing and posting permissions
- Auth0 integration: WebAuth login/logout wired to a lightweight auth session store
- Dependency Injection: Hilt provides repositories, DAOs, and network clients

## App Tour

### Hub (Feed)
- Entry: bottom-nav "Hub"
- Loads timeline posts from Room and refreshes from network in the background.
- Feed cards include author, handle, body, and reaction counts.
- If the current user owns a post, an edit icon appears.

### Create Post
- Entry: bottom-nav "Post"
- Drafts are stored in memory and validated for max length (280 chars).
- Guests cannot post; signed-in users can create local posts stored in Room.

### Search
- Entry: bottom-nav "Search"
- Debounced query (1.2s) to avoid constant network calls.
- Search runs against local cache first, then remote, and merges results.
- Tap a user to view their profile.

### Profile (My Profile)
- Entry: bottom-nav "Profile"
- Displays current session user if available; otherwise a guest placeholder.
- Can create a profile, edit it, or delete the account.
- Shows recent posts owned by the user.

### View Profile
- Navigates from Hub or Search to another user.
- Loads profile data from Room and refreshes from network.

### Auth
- Entry: bottom-nav "Auth"
- Auth0 WebAuth login/logout.
- Status is shown from DataStore-based auth session state.

## Architecture Overview

SocialHub uses a classic 3-layer structure:

- **UI layer** (Compose screens, ViewModels)
- **Data layer** (Repositories, Room, Retrofit)
- **Local session layer** (DataStore for session/user state)

Data flows are reactive where possible:

- Room DAOs expose `Flow` streams.
- ViewModels combine streams and expose `StateFlow` to Compose.
- Network refreshes are triggered in ViewModels and stored locally.

### Navigation

Routes are defined in `AppDestination` and hosted by a single `NavHost`.

Bottom navigation destinations:
- Hub
- Create Post
- Search
- My Profile
- Auth

Other routes:
- Edit User
- Edit Post
- View Profile
- Create User

The start destination is Auth.

### Dependency Injection

Hilt provides:
- `SocialHubDatabase` and DAOs
- Retrofit clients (DummyJSON)
- `UserRepository`, `PostRepository`

The application class `SocialHubApp` enables Hilt.

## Data Layer Details

### Room Database
Tables (entities):
- `users`
- `posts`
- `comments`
- `likes`
- `search_history`

Only users and posts are currently surfaced in UI. The other tables are ready for future features.

### Repositories

#### `UserRepository`
- Observes and caches users
- Searches by username/name with local + remote merge
- Builds bios from remote address, university, and company

#### `PostRepository`
- Fetches timeline and profile posts from DummyJSON
- Maps `RemotePostDto` to `PostEntity`
- Generates a realistic timestamp within the last 24 hours for remote posts
- Supports local creation, update, and delete

### Remote APIs
Remote data is sourced from DummyJSON:
- Users: `https://dummyjson.com/users`
- Posts: `https://dummyjson.com/posts`

User avatars are generated from `https://i.pravatar.cc` using the username.

## Session State

Two DataStore-backed session stores drive UX:

- `CurrentUserStore`: Tracks the current app user (front-end session)
- `Auth0SessionStore`: Tracks Auth0 login state (name/email only)

These stores are intentionally separate so you can use Auth0 without forcing a profile creation step, and vice versa.

## UI and Theming

- Compose UI is styled with a custom dark, high-contrast palette in `AppColors`.
- A subtle animated gradient is used as the background for each screen.
- Shared UI components include `PostCard`, `CreatePostCard`, and `ProfileHeader`.

## Permissions

The app requests:
- `android.permission.INTERNET`

## Build and Run

Prerequisites:
- Android Studio (or Android SDK + Gradle)
- JDK 11

Typical run steps:
1. Open the project in Android Studio.
2. Sync Gradle.
3. Run the `app` configuration on an emulator or device.

## Auth0 Configuration

Auth0 credentials are currently stored in:
- `app/src/main/res/values/strings.xml`
- Manifest placeholders in `app/build.gradle.kts`

Replace these values with your own Auth0 application settings:
- `com_auth0_domain`
- `com_auth0_client_id`
- `com_auth0_scheme`

## Project Structure

Top-level:
- `app/` Android app module
- `gradle/` dependency versions and wrapper

Key paths:
- `app/src/main/java/com/example/socialhub/ui` UI and navigation
- `app/src/main/java/com/example/socialhub/data` repositories, Room, Retrofit
- `app/src/main/java/com/example/socialhub/data/local` Room entities, DAOs, DataStore
- `app/src/main/java/com/example/socialhub/data/remote` Retrofit APIs and DTOs

## Known Gaps / Future Work

- Comments and likes are modeled in Room but not wired to UI actions.
- Search history persistence is implemented but not surfaced in UX.
- Auth0 login currently stores session state but does not attach an Auth0 profile to `UserEntity`.

## Tech Stack

- Kotlin 2.0
- Jetpack Compose + Material 3
- Navigation Compose
- Room + DataStore
- Retrofit + Gson
- Hilt
- Coroutines + Flow
- Coil

## License

See [LICENSE](LICENSE) for details.
