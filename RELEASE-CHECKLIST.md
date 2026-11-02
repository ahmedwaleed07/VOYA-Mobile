# VOYA app stores — release-blocker checklist (1 Nov 2026)

## Source & identity
- [x] Native Android source project prepared (Java / WebView)
- [x] Native iOS source project prepared (SwiftUI / WKWebView)
- [x] Android `compileSdk=36`, `targetSdk=36`
- [x] iOS Xcode project and plist prepared
- [x] HTTPS VOYA preview site configured in both apps
- [x] Local clearly labelled fallback added
- [ ] Obtain/reconcile the original VOYA web application source and production API architecture
- [ ] Replace TEMP app icons with approved original VOYA brand assets
- [ ] Verify website is reachable from target devices and all functions work in the embedded browser
- [ ] Validate the claimed original 69 languages and correct RTL behaviour

## Functionality
- [ ] Stable production login/register + phone/email verification + Apple/Facebook flows
- [ ] Account recovery, persistent sessions, secure sign-out and account deletion
- [ ] Real live camera stream ingest and playback with safe permission handling
- [ ] Broadcast scheduling, geo-tagging, privacy levels and maps
- [ ] Messages and approved Companions relationship model
- [ ] Real image/short uploads and safe media storage, content deletion and replays
- [ ] User-generated-content reports, blocking, moderation and appeals/escalations
- [ ] Gift payments through appropriate store systems if launching with purchasable digital gifts
- [ ] Streaming costs, bandwidth constraints and monitoring

## Android / Google Play
- [ ] Verify API 36 behaviour on Android 16+ devices and 16-KB compatibility if native libraries added
- [ ] Complete Data Safety, content rating, store listing, original screenshots, privacy URL
- [ ] Provide full account deletion pathways and privacy policy
- [ ] Configure upload key & Play App Signing, build a signed release AAB
- [ ] Conduct internal/closed testing as required by the account
- [ ] Verify all runtime permissions and file upload on physical devices

## iOS / Apple
- [ ] Build with Xcode 26+ / iOS 26+ SDK on macOS
- [ ] Set Apple Developer Team / certificates / Bundle ID
- [ ] Implement compliant Sign in with Apple and third-party OAuth where required
- [ ] Confirm App Store minimum functionality beyond a simple wrapped website
- [ ] Complete App Privacy declarations, age rating and review access
- [ ] Test on real iPhone; distribute through TestFlight
- [ ] Archive and sign a distributable IPA via Xcode / App Store Connect

## Status note
Both archives contain SOURCE PROJECTS, not compiled signed mobile releases. WebView features cannot be treated as complete unless confirmed with the real website and backend. All product decisions remain the original VOYA specification.
