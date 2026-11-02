# VOYA build verification — 1 November 2026

| Target | Prepared | Actual compiled build | Installable / store package |
|---|---|---|---|
| Android Java project | Yes | Not possible here (SDK/Gradle missing) | No APK / AAB yet |
| iOS SwiftUI project | Yes | Swift syntax and Xcode project validated; Xcode unavailable | No IPA / TestFlight upload yet |
| GitHub Actions pipeline | Yes | Not run; VOYA repository not available in connected GitHub | Awaiting separate VOYA repo |

**Static validation performed:** XML manifests/resources parse, iOS Info.plist parses, Xcode PBX project parses, Swift source syntax parses on Linux, and `scripts/verify_source.py` checks the files and packaging assumptions.

**Important:** These are WebView development shells connected to an existing web URL. They are not full native VOYA implementations. The link could not be reached from the available inspection environment and needs a physical-device test. Do not upload as a production app yet.

**Store blockers:** real login/OAuth, live broadcasting, backend, user content moderation, privacy and account deletion, original approved app icons, signing, App Store minimum functionality, Google Play declarations, billing if digital gifts are sold, and device/beta testing.
