# Aura (أورا) — Native Android Social Network

Aura is a modern, luxury social network engineered with Android Native, Kotlin, and Jetpack Compose. Designed from the ground up to redefine authentic digital connections, Aura rejects generic feeds and superficial metrics in favor of curated private circles, context-aware social layers, mindful quiet feeds, and high-fidelity media storytelling.

---

## Key Pillars & Distinctive Features

1. **Private Circles (الدوائر الخاصة)**
   - Segment audiences privately into tailored circles (*Inner Circle*, *Design Studio*, *Tech Syndicate*, *Family & Kin*).
   - Zero member disclosure: Circle members can never inspect the identity or roster of other members.

2. **Social Layers (الطبقات الاجتماعية)**
   - Single-account contextual switching across **Personal**, **Professional**, **Creative**, and **Tech & Gaming** modes.
   - Adjusts content feed discovery and profile presence without requiring disconnected pseudo-accounts.

3. **Quiet Feed (النمط الهادئ)**
   - One-tap toggle that strips away vanity metrics (like counts, follower counts, trending counters) to provide a serene, distraction-free reading experience.

4. **Mood Feed (خلاصة المزاج)**
   - Content preference filters (*Relax*, *Discover*, *Learn*, *Laugh*, *Connect*, *Focus*) to align reading sessions with user intention.

5. **Smart Catch-Up (الاستدراك الذكي)**
   - High-density returning summary curating missed discussions, circle updates, and direct communications instead of forcing infinite doom-scrolling.

6. **Time-Limited Posts (المنشورات محددة الأجل)**
   - Native support for permanent, 24-hour, 3-day, and 7-day auto-archiving post lifetimes.

7. **Collaborative Posts (المنشورات التشاركية)**
   - Multi-creator co-authorship with shared interaction feeds.

8. **Live Discussion Rooms (غرف الحوار المباشرة)**
   - Temporary, topic-specific discussion spaces linked directly to trending posts and community themes.

9. **High-Fidelity Messaging & Voice Notes**
   - Real-time conversation threads with live audio waveforms, play/pause controls, delivery/read receipts, and circle group messaging.

10. **Bilingual by Design (Arabic RTL & English LTR)**
    - Comprehensive resource-driven localization with native Arabic typography, proper layout mirroring, and culturally resonant phrasing.

11. **Obsidian Luxury Design System**
    - Deep obsidian dark palette (`#08090D`, `#10121A`), champagne gold accents (`#E5B869`), violet radiance (`#8B5CF6`), subtle borders, refined line heights, and haptic feedback.

---

## Technical Stack & Architecture

- **Language:** Kotlin 2.x
- **UI Toolkit:** Jetpack Compose with Material 3 foundation and custom luxury design system
- **Architecture:** Clean Architecture + MVVM with reactive `StateFlow` and single source of truth
- **Local Persistence:** Room Database with `CachedPostEntity` and `PostDao` for offline-first readiness
- **Image Pipeline:** Coil with crossfade caching and responsive aspect ratio scaling
- **Async Concurrency:** Kotlin Coroutines & Flow
- **Edge-to-Edge:** Native `WindowInsets` and dynamic system bar contrast integration
- **Testing:** Local JVM Robolectric unit tests for CUJs and repository mutations

---

## Project Structure

```
com.example/
├── MainActivity.kt                 # Root activity with predictive back handling & edge-to-edge
├── core/
│   ├── database/                   # Room database, entities, and DAOs
│   │   ├── AuraDatabase.kt
│   │   ├── CachedPostEntity.kt
│   │   └── PostDao.kt
│   ├── designsystem/               # Reusable luxury UI components
│   │   └── components/
│   │       ├── AuraAvatar.kt       # Story ring, verified checkmark, status dot
│   │       ├── AuraBottomBar.kt    # Docked luxury navigation with create button
│   │       ├── AuraButton.kt       # Primary, secondary, haptic feedback
│   │       ├── AuraPostCard.kt     # Interactive card with quiet mode & room launchers
│   │       ├── AuraStates.kt       # Shimmer skeleton, empty states, offline banner
│   │       └── AuraTopBar.kt       # Layer switcher pill, quiet mode toggle, catch-up
│   ├── model/                      # Immutable domain models (Post, User, Circle, Layer, etc.)
│   └── repository/                 # Reactive repository with optimistic UI mutations
├── features/
│   ├── auth/                       # Onboarding pager, Sign In, Sign Up, Interests setup
│   ├── home/                       # Stories, Mood filter, Feed tabs, Catch-Up sheet
│   ├── clips/                      # Vertical full-screen short video player with double-tap like
│   ├── discover/                   # Instant search, trending hashtags, active communities
│   ├── create/                     # Post composer with photo filters, circle target, expiration
│   ├── chat/                       # Direct chats, circle chats, interactive voice notes
│   ├── profile/                    # Editorial cover, highlights, tabs, edit sheet
│   └── settings/                   # Mindful toggles, security center, theme, language
└── ui/theme/                       # Luxury Obsidian palette, typography, shapes
```
