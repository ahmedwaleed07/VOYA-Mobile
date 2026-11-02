#!/usr/bin/env bash
set -euo pipefail
if [[ "$(uname -s)" != "Darwin" ]] || ! command -v xcodebuild >/dev/null; then
  echo "iOS archive requires macOS, Xcode 26+, and Apple signing credentials." >&2
  exit 2
fi
if [[ -z "${APPLE_DEVELOPMENT_TEAM:-}" ]]; then
  echo "Set APPLE_DEVELOPMENT_TEAM to your Apple Developer Team ID (do not commit private keys)." >&2
  exit 2
fi
cd "$(dirname "$0")/.."
xcodebuild -project iOS/VOYA.xcodeproj -scheme VOYA -configuration Release \
  -destination 'generic/platform=iOS' -archivePath build/VOYA.xcarchive \
  DEVELOPMENT_TEAM="$APPLE_DEVELOPMENT_TEAM" \
  CODE_SIGN_STYLE=Automatic archive
printf 'Signed archive if signing succeeded: %s\n' "$(pwd)/build/VOYA.xcarchive"
echo 'Next: Xcode Organizer → Distribute App → App Store Connect → Upload (TestFlight).'
