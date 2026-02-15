# Release QA Checklist

Last run: 2026-02-15

## 1) Build and package verification (done)
- [x] `:app:assembleRelease` passed.
- [x] `:app:lintVitalRelease` passed (ran within assemble task).
- [x] `-p backend build` passed.
- [x] `-p backend installDist` passed.
- [x] Release artifact generated: `app/build/outputs/apk/release/app-release-unsigned.apk` (~40.7 MB).

## 2) Build warnings to keep in backlog (non-blocking for debug QA)
- [ ] Native strip warning: `libdatastore_shared_counter.so`, `libjingle_peerconnection_so.so`.
- [ ] Kapt warning: Kotlin 2.0 fallback to 1.9.
- [ ] Deprecation warning in billing (`enablePendingPurchases()`).
- [ ] Gradle JVM warning: restricted `java.lang.System::load`.

## 3) Blocking gaps before Play Store publish
- [ ] Signed release build (`app-release-unsigned.apk` means no release signing yet).
- [ ] Unit test task currently broken: `:app:testDebugUnitTest` fails with `Type T not present`.
- [ ] Final Play Console metadata/assets check.
- [ ] Final policy pass (privacy/data safety/content moderation declarations).

## 4) Manual functional QA matrix (run on debug + release candidate)
- [ ] Auth/session: signup, OTP, login, refresh token, logout.
- [ ] Home/rooms: room list, filters, join/open, reconnect.
- [ ] Room realtime: seat sync, seat request, chat delivery, unread badges.
- [ ] Gifts/economy: send gift, overlay, wallet changes, transaction logs.
- [ ] DM/system messages: send/receive/read-state update.
- [ ] Billing: purchase flow + server receipt validation + coin credit.
- [ ] Role dashboards: Admin, Agency, Reseller (permissions + actions).
- [ ] Rewards/Rocket/My Menu flows: claim/request history/state updates.

## 5) Release readiness exit criteria
- [ ] No crash/ANR in smoke tests (Android 10, 12, 14+ at minimum).
- [ ] Production backend reachable from app domain with stable 2xx responses.
- [ ] Signed AAB generated and upload-tested in internal track.
- [ ] Staged rollout plan and rollback plan documented.

## 6) Visual QA matrix (current UI polish baseline)
Legend:
- `PASS`: layout, spacing, icon sizing, typography hierarchy aligned.
- `PASS*`: aligned, but final asset replacement is still pending.
- `FOLLOW-UP`: functional UI exists but still needs another polish pass with final assets/content.

### Core navigation screens
- [x] `PASS*` Splash
- [x] `PASS*` Login
- [x] `PASS*` Signup
- [x] `PASS*` Onboarding Country
- [x] `PASS*` Onboarding Profile
- [x] `PASS*` Rooms (Meu/Popular/Postagens tabs + room list)
- [x] `PASS*` Inbox (Mensagem/Amigos/Familia tabs + row alignment)
- [x] `PASS*` Profile (Perfil/Editar/Menu tabs + menu grid)

### Room flow screens
- [x] `PASS*` Room Detail (header actions + section menu + action bar)
- [x] `PASS*` Room Audio
- [x] `PASS*` Room Seats
- [x] `PASS*` Room Chat
- [x] `PASS*` Room Gifts
- [x] `PASS*` Room Rewards / Rocket
- [x] `PASS*` Room Tools

### Utility and role screens
- [x] `PASS*` Wallet
- [x] `PASS*` Recharge
- [x] `PASS*` Backpack
- [x] `PASS*` Settings
- [x] `PASS*` VIP
- [x] `PASS*` Star Path
- [x] `PASS*` Admin Dashboard
- [x] `PASS*` Agency Tools
- [x] `PASS*` Reseller Tools

### Game screens
- [x] `PASS*` Mini Games Hub
- [x] `PASS*` Game Detail

### Row/component consistency
- [x] `PASS*` Common item rows (room, inbox, follower, post, wallet package, admin/agency rows, game rows, search rows, chat rows)
- [x] `PASS*` Icon optical alignment (avatars, trailing action icons, badges)
- [x] `PASS*` Shared typography hierarchy (headline/section/body/muted + list title/body/meta/badge)

## 7) Follow-up execution plan (3 days)
Tracking format:
- `Owner`: person responsible
- `Start`: planned start date (`YYYY-MM-DD`)
- `ETA`: target completion date (`YYYY-MM-DD`)
- `Status`: `Not started` / `In progress` / `Blocked` / `Done`

### Day 1 - Asset replacement + contrast validation
- [ ] Replace temporary icons/backgrounds with final production assets. | Owner: ___ | Start: ___ | ETA: ___ | Status: Not started
- [ ] Re-check contrast for text, badges, chips, and buttons on all updated backgrounds. | Owner: ___ | Start: ___ | ETA: ___ | Status: Not started
- [ ] Re-verify icon optical balance (left icons, trailing icons, unread badges) after asset swap. | Owner: ___ | Start: ___ | ETA: ___ | Status: Not started
- [ ] Acceptance: no clipped assets, no low-contrast text, no oversized/undersized icons. | Owner: QA Lead ___ | Start: ___ | ETA: ___ | Status: Not started

### Day 2 - Localization spacing pass
- [ ] Validate all primary screens in Portuguese + English. | Owner: ___ | Start: ___ | ETA: ___ | Status: Not started
- [ ] Re-tune paddings/widths for long labels and dynamic values (names, IDs, role labels, tabs). | Owner: ___ | Start: ___ | ETA: ___ | Status: Not started
- [ ] Ensure no truncation in critical actions (buttons, nav labels, tab labels, dialog actions). | Owner: ___ | Start: ___ | ETA: ___ | Status: Not started
- [ ] Acceptance: no overlap/truncation in core flows for both locales. | Owner: QA Lead ___ | Start: ___ | ETA: ___ | Status: Not started

### Day 3 - Device matrix visual sweep
- [ ] Run visual QA on small phone, tall phone, and tablet/emulator profiles. | Owner: ___ | Start: ___ | ETA: ___ | Status: Not started
- [ ] Check vertical rhythm: section spacing, row heights, list density, sticky/fixed headers. | Owner: ___ | Start: ___ | ETA: ___ | Status: Not started
- [ ] Validate system UI interactions: keyboard open/close, back navigation, orientation changes. | Owner: ___ | Start: ___ | ETA: ___ | Status: Not started
- [ ] Acceptance: UI is consistent across target form factors with no layout breakage. | Owner: QA Lead ___ | Start: ___ | ETA: ___ | Status: Not started

### Close criteria for follow-up block
- [ ] All Day 1-3 tasks completed. | Owner: PM ___ | Start: ___ | ETA: ___ | Status: Not started
- [ ] Updated screenshots captured for core flows after final assets. | Owner: QA ___ | Start: ___ | ETA: ___ | Status: Not started
- [ ] Visual QA matrix in this file moved from `PASS*` to `PASS` where confirmed. | Owner: QA Lead ___ | Start: ___ | ETA: ___ | Status: Not started
