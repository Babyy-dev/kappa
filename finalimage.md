# FINAL IMAGE & ICON LIST (ALL SCREENS)

This list is the single source of truth for **every screen** and **every icon** with **exact sizes**.
All sizes are in **px** (pixel). Use WebP where possible (except icons that must be PNG).

---

## ANDROID PHONE SIZE RULES (IMPORTANT)
- All sizes listed below are **XXHDPI (3x)** target sizes (1080x1920 is 360x640 dp).
- Generate all densities from these sizes:
  - **MDPI (1x)** = size / 3
  - **HDPI (1.5x)** = size / 2
  - **XHDPI (2x)** = size * 2 / 3
  - **XXHDPI (3x)** = size (as listed)
  - **XXXHDPI (4x)** = size * 4 / 3
- Put large photos/backgrounds in **drawable-nodpi** (keep 1080x1920 or 1080x360).
- Icons should have **all densities** in drawable-mdpi/hdpi/xhdpi/xxhdpi/xxxhdpi.
- Quick conversion: **dp = px / 3** (because list is XXHDPI).

Example:
- `ic_nav_rooms.png` listed 128x128 (XXHDPI)
  - mdpi: 43x43
  - hdpi: 64x64
  - xhdpi: 86x86
  - xxhdpi: 128x128
  - xxxhdpi: 171x171

---

## 0) GLOBAL / APP‑LEVEL
- App logo (adaptive)
  - `logo.png` (1024x1024)
  - `logo_foreground.png` (432x432)
  - `logo_background.png` (432x432)
- Splash background: `splash_bg.png` (1080x1920)
- Loading background: `loading_bg.png` (1080x1920)
- Country flags (ISO full set): `flag_xx.png` (48x48)

---

## 1) AUTH / LOGIN
- Screen background: `auth_bg.png` (1080x1920)
- Logo (wide): `logo_wide.png` (800x400)
- Input icons (optional): `ic_user.png` (96x96), `ic_lock.png` (96x96), `ic_phone.png` (96x96)
- OTP banner (optional): `otp_banner.png` (900x240)

---

## 2) SIGNUP
- Screen background: `auth_bg.png` (1080x1920)
- Logo (wide): `logo_wide.png` (800x400)
- OTP banner (optional): `otp_banner.png` (900x240)

---

## 3) ONBOARDING (COUNTRY + PROFILE)
- Background: `onboarding_bg.png` (1080x1920)
- Country list header: `country_header.png` (900x200)
- Profile header: `profile_header.png` (900x200)
- Default avatar: `avatar_default.png` (256x256)
- Avatar frame (default): `avatar_frame.png` (320x320)

---

## 4) HOME / ROOMS (POPULAR)
- Top art header: `potalive_top_header.png` (632x170)
- Tab header background: `potalive_header.png` (697x161)
- Bottom nav band: `potalive_bottom_nav.png` (1127x330)
- Rotating banner images: `banner_xxx.jpg` (1080x360)
- Banner overlay: `banner_overlay.png` (1080x360)
- Banner dots: `ic_banner_dot_active.png` (24x24), `ic_banner_dot_inactive.png` (24x24)
- Room card background: `room_card_bg.png` (640x360)
- Room card thumbnails: `room_thumb_xxx.jpg` (640x360)
- Room activity badges: `badge_live.png` (96x96), `badge_hot.png` (96x96)
- Top-right icons:
  - `ic_my_room.png` (96x96)
  - `ic_notification.png` (96x96)
  - `ic_search.png` (96x96)
- Filter chips bg (optional): `chip_bg.png` (200x80)

---

## 5) ROOM DETAIL (AUDIO / SEATS / CHAT / GIFTS / TOOLS)
- Room background: `room_bg_xxx.jpg` (1080x1920)
- Room header ornament: `ic_room_ornament.png` (140x140)
- Room tabs icons:
  - `ic_room_audio.png` (170x170)
  - `ic_room_seats.png` (158x158)
  - `ic_room_chat.png` (176x176)
  - `ic_room_gifts.png` (158x158)
  - `ic_room_tools.png` (192x192)
- Seat icons:
  - `seat_empty.png` (128x128)
  - `seat_occupied.png` (128x128)
  - `seat_blocked.png` (128x128)
- Seat request indicator: `ic_seat_request.png` (96x96)
- Room action bar icons:
  - `ic_room_chat.png` (96x96)
  - `ic_room_mic.png` (96x96)
  - `ic_room_gift.png` (96x96)
  - `ic_room_tools.png` (96x96)
  - `ic_room_game.png` (96x96)

---

## 6) ROOM CHAT
- Chat bubble self: `chat_bubble_self.png` (stretchable)
- Chat bubble other: `chat_bubble_other.png` (stretchable)
- Chat bubble system: `chat_bubble_system.png` (stretchable)
- Mention highlight: `mention_highlight.png` (stretchable)

---

## 7) GIFTS
- Gift category icons:
  - `gift_cat_fixed.png` (128x128)
  - `gift_cat_multiplier.png` (128x128)
- Gift item icons: `gift_xxx.png` (128x128)
- Gift send button: `gift_send_btn.png` (400x140)
- Gift animations (sprite sheets):
  - `gift_burst.png` (sprite sheet)
  - `gift_coin.png` (sprite sheet)
  - `gift_box.png` (sprite sheet)
- Multiplier effects:
  - `fx_multiplier_2x.png` (256x256)
  - `fx_multiplier_5x.png` (256x256)
  - `fx_multiplier_10x.png` (256x256)
  - `fx_multiplier_100x.png` (256x256)
  - `fx_multiplier_250x.png` (256x256)
  - `fx_multiplier_500x.png` (256x256)
  - `fx_multiplier_1000x.png` (256x256)

---

## 8) GLOBAL 100x+ OVERLAY
- Overlay background: `global_win_bg.png` (1080x360)
- Overlay frame: `global_win_frame.png` (1080x360)
- Glow / particles: `fx_global_glow.png` (512x512), `fx_global_particles.png` (512x512)
- Trophy badge: `badge_100x.png` (256x256)

---

## 9) INBOX / MESSAGES
- Inbox header art: `inbox_header.png` (900x200)
- Message icons:
  - `ic_message.png` (96x96)
  - `ic_message_unread.png` (96x96)
- Friend status dot: `ic_status_online.png` (24x24), `ic_status_offline.png` (24x24)
- VIP badge (small): `vip_badge_small.png` (64x64)

---

## 10) FRIENDS / FAMILY
- Friends header: `friends_header.png` (900x200)
- Family header: `family_header.png` (900x200)
- Family badge: `family_badge.png` (128x128)
- Join/Approve icons: `ic_accept.png` (96x96), `ic_reject.png` (96x96)

---

## 11) PROFILE (ME)
- Profile header art: `profile_header.png` (900x200)
- Profile frame default: `profile_frame_default.png` (256x256)
- Profile frame VIP: `profile_frame_vip.png` (256x256)
- Edit icon: `ic_edit.png` (96x96)
- Settings icon: `ic_settings.png` (96x96)

---

## 12) MY MENU / WALLET / VIP / STAR PATH
- Wallet icons:
  - `ic_coins.png` (96x96)
  - `ic_diamonds.png` (96x96)
- Backpack slot: `item_slot.png` (128x128)
- VIP badges: `vip_1.png` → `vip_n.png` (96x96)
- Star Path background: `starpath_bg.png` (1080x1920)
- Star icons: `star_locked.png` (96x96), `star_unlocked.png` (96x96)

---

## 13) ADMIN DASHBOARD
- Admin header: `admin_header.png` (900x200)
- Admin menu icons:
  - `ic_admin_banner.png` (96x96)
  - `ic_admin_users.png` (96x96)
  - `ic_admin_agencies.png` (96x96)
- Toggle switch:
  - `toggle_on.png` (96x96)
  - `toggle_off.png` (96x96)

---

## 14) AGENCY DASHBOARD
- Agency header: `agency_header.png` (1080x360)
- Agency icons:
  - `ic_members.png` (96x96)
  - `ic_revenue.png` (96x96)
  - `ic_withdraw.png` (96x96)
  - `ic_exchange.png` (96x96)
  - `ic_hosts.png` (96x96)

---

## 15) RESELLER DASHBOARD
- Reseller header: `reseller_header.png` (900x200)
- Reseller icons:
  - `ic_sellers.png` (96x96)
  - `ic_limits.png` (96x96)
  - `ic_sales.png` (96x96)
  - `ic_proof.png` (96x96)
  - `ic_send_coins.png` (96x96)

---

## 16) MINI‑GAMES
- Game hub banner: `game_hub_banner.png` (1080x360)
- Game icons: `game_xxx.png` (128x128)
- Countdown / timer ring: `game_timer.png` (256x256)
- Reward badge: `game_reward.png` (256x256)

---

## 17) COMMON UI ELEMENTS
- Primary button bg: `btn_primary.png` (697x161)
- Secondary button bg: `btn_secondary.png` (697x161)
- Dialog bg: `dialog_bg.png` (stretchable)
- Divider: `divider.png` (stretchable)
- Search input bg: `search_bg.png` (900x120)
- Notification badge: `ic_badge.png` (32x32)

---

## 18) BOTTOM NAVIGATION ICONS
- Rooms: `ic_nav_rooms.png` (128x128)
- Messages: `ic_nav_messages.png` (128x128)
- Profile: `ic_nav_profile.png` (128x128)

---

## FINAL NOTES
- All assets must be **unique** (no duplicates with different names).
- Provide both **normal** and **@2x/@3x** if you want precise scaling.
- Prefer **WebP** for photos and large backgrounds; **PNG** for icons.
