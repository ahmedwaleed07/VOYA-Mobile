# VOYA — Google Play upload / رفع Google Play

**Status: internal-test preparation only; NOT approved for production release.**

## Current verified facts
- Package/application ID: `com.voya.app`.
- Android compile and target SDK: API 36.
- Current Android host is a WebView for `https://voya-app.funpony.chatgpt.site/`.
- Previous GitHub Actions successfully built Debug APK; this new workflow builds a Release **AAB**.
- Store upload is **not completed** and the account cannot be accessed through this ChatGPT connection.

## How to prepare a signed AAB
1. Keep the release upload keystore **private** and securely backed up. Do not upload it to this public repository, to PRs, or to the chat.
2. Generate a new upload key in Android Studio or with `keytool`; configure Play App Signing.
3. In GitHub repository **Settings → Secrets and variables → Actions**, add:
   - `VOYA_UPLOAD_KEYSTORE_B64`: Base64-encoded content of the upload keystore. No line breaks.
   - `VOYA_UPLOAD_STORE_PASSWORD`: Keystore password.
   - `VOYA_UPLOAD_KEY_ALIAS`: Signing alias.
   - `VOYA_UPLOAD_KEY_PASSWORD`: Key password.
4. Run workflow **VOYA Android Play Bundle Preparation**. It creates `VOYA-Android-Play-SIGNED-AAB` only when all four secrets are present; otherwise it creates clearly labeled `VOYA-Android-UNSIGNED-AAB-NOT-FOR-UPLOAD`.
5. Confirm the AAB signature and bundle ID before uploading. Do not upload unsigned or debug files.
6. Create app in Play Console, enable Play App Signing, complete mandatory declarations and accurate store listing, then upload signed AAB to **Internal testing** for review.

## Mandatory release blockers for VOYA
- The original VOYA logo is **not** used in the native icon; the placeholder must be replaced.
- Website/backend real authentication, live streaming, camera/mic/geo permissions and media uploads remain unverified.
- Terms & privacy policy with public URL and in-app access; Data Safety; account deletion in-app AND via accessible website.
- UGC report/block/moderation; accurate content rating; valid reviewer credentials.
- Genuine in-app screenshots, contact/support email and production-ready store listing.
- Device tests and security review. Pricing/gifts must comply with store billing rules if enabled.
- New personal Play accounts may require >=12 opted-in closed testers for >=14 consecutive days before requesting Production access.

## Play Console
https://play.google.com/console

## Sources
- https://support.google.com/googleplay/android-developer/answer/9859152
- https://support.google.com/googleplay/android-developer/answer/9842756
- https://support.google.com/googleplay/android-developer/answer/9876937
- https://support.google.com/googleplay/android-developer/answer/13327111
- https://support.google.com/googleplay/android-developer/answer/14151465
