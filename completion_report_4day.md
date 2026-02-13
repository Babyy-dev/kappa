# Completion Report (4‑Day Plan to Publish)

Goal: If we follow this plan, the app will be **complete and ready for Google Play submission** at the end of Day 4.

---

## What’s missing now (blocking Google Play)
1) **Authentication completeness**
   - OTP + phone validation must be verified on production backend.
   - Session refresh + invalid session handling must be stable.

2) **Core functional parity**
   - Rooms: real data, filters, join flow, seat sync for 28 seats.
   - Chat: real-time delivery + unread counts.
   - Gifts: correct catalog, send modes, overlay pipeline.

3) **UI / Visual parity (critical)**
   - Potalive‑style headers, backgrounds, nav bars must be consistent across Home/Room/Inbox/Profile.
   - Icons must be upgraded (no overlap, correct spacing, visible text).
   - Assets must be complete and applied (see `finalimage.md`).

4) **Economy + billing**
   - Google Play Billing must be fully validated server-side.
   - Coins only after validation.

5) **Admin/Agency/Reseller**
   - All dashboards must be functional (not UI-only).
   - Audit logs, configuration, and approval flows must work.

6) **Stability & Store readiness**
   - No crash flows.
   - Privacy policy + compliance checks.
   - Release build must pass QA.

---

## 4‑Day Completion Plan

### Day 1 — Backend + Auth + Critical Flows
Deliverable: **Auth & core flows work end‑to‑end**
- Verify OTP + phone validation on VPS.
- Confirm login/signup/guest flows with real backend.
- Fix any invalid session/refresh token bugs.
- Validate rooms list fetch, room join, and seat sync (28 seats).
- UI: fix login/signup/onboarding UI consistency (labels, spacing, colors).

### Day 2 — Messaging + Gifts + Room Realtime
Deliverable: **Messaging + gifts fully functional**
- Inbox unread counts + friend search.
- Room chat realtime (send/receive across accounts).
- Gift catalog + send modes (self/all/selected).
- Gift overlay in room (basic animation + receipt event).
- UI: apply Potalive‑style headers/backgrounds on Inbox + Room.

### Day 3 — Economy + Billing + Dashboards
Deliverable: **Payments + admin tools stable**
- Google Play Billing flow validated server‑side.
- Coins credited only after validation.
- Admin: RTP/House Edge, per‑game/per‑user configs, lock rules, audit logs.
- Agency/Reseller: approvals, limits, sales, proof upload.
- UI: dashboard polish (cards, icons, spacing), no overlap.

### Day 4 — QA + Release Checklist
Deliverable: **Play‑store ready build**
- Full regression: login/onboarding/rooms/chat/gifts/dashboards.
- Crash/ANR check, performance pass.
- Release build signed, versioned, and aligned with store policies.
- Final smoke test on device + emulator.
- UI: final pass across all screens to match reference.

---

## Definition of “Completed”
The app is considered completed when:
- All core flows operate on the real backend.
- No critical crashes in auth/rooms/chat/gifts.
- Billing validated and secure.
- Dashboards are functional, not UI‑only.
- Release build passes QA and store checklist.

---

If you want, I can map each missing item to code files or open tasks next. 
