# VOYA Mobile — Android and iOS source projects

**Status: development preview / NOT a verified production or store-release build.**

This package contains two distinct native host applications that open VOYA's existing website in a mobile WebView while using the site's original web interface, logo, colors and language selection rather than substituting a new app UI. Both include a clearly marked local offline fallback showing the intended information architecture **without impersonating real live streams, accounts, or messages**. This is a pragmatic starting point because the canonical VOYA web source, original app icon, authentication backend and streaming service were not available for inspection in this session.

- Website: https://voya-app.funpony.chatgpt.site/
- Brand: VOYA — LIVE TRAVEL EXPLORATION / Travel while you stay
- Ownership: Powered by ASAS 360°
- Palette: `#171717` / `#F5F3EE` / `#77736D` / `#C8E64A`
- Package/Bundle identifier proposal: `com.voya.app` (verify availability in both stores)

## Android

Files: `Android/` — native Java Android application, min SDK 26, compile/target API 36, Android Gradle Plugin 8.13.2 and Gradle 8.13.

1. Install Android Studio, JDK 17, Android API 36 SDK and Gradle 8.13.
2. Open the `Android` folder in Android Studio, sync Gradle; it can use your local Gradle 8.13 installation. The checked-in wrapper *properties* are included, but the Gradle wrapper JAR is not available in this environment; you can create it on your build machine with `gradle wrapper --gradle-version 8.13`.
3. Build with `gradle :app:assembleDebug` inside `Android/`, or run the included GitHub Actions workflow. On a successful build the debug installer is `Android/app/build/outputs/apk/debug/app-debug.apk`.
4. To distribute publicly, configure release signing and build the signed Android App Bundle `gradle :app:bundleRelease`. Upload the AAB to Play Console, complete listing and policy forms and perform internal/closed testing as required.

Native bridge includes web file selection, requested runtime camera/microphone access, geolocation permission, safe HTTPS-only browsing, external link opening and retry/offline fallback. These features depend on the website implementing them.

## iOS

Files: `iOS/` — SwiftUI / WKWebView app project targeting iOS 15+ and designed for Xcode 26 or later / iOS 26 SDK or later.

1. On a Mac with **Xcode 26 or later**, open `iOS/VOYA.xcodeproj`.
2. Select the `VOYA` target → **Signing & Capabilities** → select your Apple Developer Team. Confirm Bundle Identifier `com.voya.app` or change it to an available identifier.
3. Choose an iPhone simulator or device and select **Run**.
4. Choose **Product → Archive** to produce a device archive for TestFlight/App Store Connect, after configuring distribution certificates/profiles and fixing the remaining release blockers.
5. The GitHub Actions workflow builds an **unsigned iOS Simulator `.app`**, NOT an iPhone-installable `.ipa`.

WKWebView provides inline media rendering and iOS camera/microphone permission prompts for the trusted VOYA domain. The web page can use the normal HTML file picker. Social identity-provider authentication requires additional native OAuth work.

## CRITICAL: not a shipping app yet

This package **does not contain** a production backend, actual live video service, real geolocated streams, server-side moderation, active gift purchases, signed APK/AAB or signed iOS IPA. The provided web URL was not accessible to the inspection environment, so real-device loading and camera/mic behaviour have not been verified. The fallback page is clearly marked as a nonfunctional offline preview.

### Original brand identity

The current iOS AppIcon PNG and Android `voya_launcher.xml` are **temporary typographic placeholders**. Replace them with exported icon files from the **approved original VOYA logo** before submission; do not reinterpret/redraw the logo or alter the web UI. The native fallback uses the existing color palette only. The live site keeps its own original branding.

### Important release blockers

See `RELEASE-CHECKLIST.md`. Most critically: native auth for OAuth providers; functional backend, live streams, secure uploads, message/companion permissions, maps; original approved icons and accurate screenshots; account deletion flows; user-generated content moderation, reporting/blocking; privacy/terms; testing; signing; and platform-specific payment policies for digital gifts. Apple may reject a simple web-only wrapper under the minimum-functionality rule; the production version needs substantive app-native functionality or an independently acceptable platform experience.

## Continuous integration

The included `.github/workflows/mobile-build.yml` can build a **debug Android APK** and an **unsigned iOS simulator app** after you upload these files to a GitHub repository and trigger the workflow. Artifacts appear on the workflow run page. The workflow has not been executed here and its outputs are not included in these archives.

No secret keys, signing material or private API credentials are included.
