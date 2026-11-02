#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/../Android"
if [[ -z "${ANDROID_HOME:-${ANDROID_SDK_ROOT:-}}" ]]; then
  echo "Android SDK missing: set ANDROID_HOME or ANDROID_SDK_ROOT to a valid SDK containing platform android-36." >&2
  exit 2
fi
if command -v gradle >/dev/null; then
  gradle :app:assembleDebug --no-daemon
elif [[ -x ./gradlew ]]; then
  ./gradlew :app:assembleDebug --no-daemon
else
  echo "Gradle 8.13 is required. Install Gradle 8.13 or run via GitHub Actions." >&2
  exit 2
fi
printf 'Debug APK: %s\n' "$(pwd)/app/build/outputs/apk/debug/app-debug.apk"
