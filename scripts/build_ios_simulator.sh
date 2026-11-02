#!/usr/bin/env bash
set -euo pipefail
if [[ "$(uname -s)" != "Darwin" ]] || ! command -v xcodebuild >/dev/null; then
  echo "iOS compilation requires macOS and Xcode 26 or newer." >&2
  exit 2
fi
cd "$(dirname "$0")/.."
xcodebuild -project iOS/VOYA.xcodeproj -scheme VOYA -configuration Debug \
  -destination 'generic/platform=iOS Simulator' -derivedDataPath build/ios \
  CODE_SIGNING_ALLOWED=NO build
printf 'Simulator-only app: %s\n' "$(pwd)/build/ios/Build/Products/Debug-iphonesimulator/VOYA.app"
